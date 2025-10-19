package utils;

import client.IngredientClient;
import com.google.gson.Gson;
import io.restassured.response.Response;
import model.IngredientListResponse;
import model.Ingredient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IngredientGenerator {

    private static IngredientClient ingredientClient = new IngredientClient();
    private static Gson gson = new Gson();
    private static List<Ingredient> ingredientList;

    static {
        // Получаем список ингредиентов с сервера
        Response response = ingredientClient.getIngredients();
        ingredientList = gson.fromJson(response.asString(), IngredientListResponse.class).getData();
    }


     // Возвращает случайные N ингредиентов из всех доступных
    public static List<String> getRandomIngredientIds(int count) {
        Collections.shuffle(ingredientList);
        return IntStream.range(0, Math.min(count, ingredientList.size()))
                .mapToObj(i -> ingredientList.get(i).get_id())
                .collect(Collectors.toList());
    }
}
