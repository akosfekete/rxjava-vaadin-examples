package org.vaadin.example;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.HttpClientRequest;
import io.vertx.rxjava3.core.http.HttpClientResponse;

@Route
public class MainView extends VerticalLayout {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Construct a new Vaadin view.
	 * <p>
	 * Build the initial UI state for the user accessing the application.
	 */
	public MainView() {
		Single<List<Post>> request = httpRequest();
		Grid<Post> postGrid = new Grid<>(Post.class);
		Button button = new Button("Http request", e -> {
			postGrid.setItems(List.of());
			request.subscribe(responseBody -> getUI().ifPresent(ui -> ui.access(() -> postGrid.setItems(responseBody))));
		});
		add(button);
		add(postGrid);
	}

	private Single<List<Post>> httpRequest() {
		Vertx vertx = Vertx.vertx();
		HttpClient client = vertx.createHttpClient();
		return client.rxRequest(HttpMethod.GET, 80, "jsonplaceholder.typicode.com", "/posts")
				.flatMap(HttpClientRequest::rxSend)
				.flatMap(HttpClientResponse::body)
				.map(Object::toString)
				.map(it -> MAPPER.readValue(it, new TypeReference<>() {}));
	}
}
