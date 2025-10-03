package dev.onat.onalps.service;

import ai.fal.client.AsyncFalClient;
import ai.fal.client.Output;
import ai.fal.client.SubscribeOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.onat.onalps.dto.request.CreatePostApiRequestDto;
import dev.onat.onalps.dto.response.CreatePostApiResponseDto;
import dev.onat.onalps.utils.Constants;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncFalService {
    private final AsyncFalClient falClient;
    private final RestTemplate restTemplate = new RestTemplate();

    AsyncFalService(AsyncFalClient falClient) {
        this.falClient = falClient;
    }

    CompletableFuture<String> asyncGeneratePreview(String prompt) {
        Map<String, Object> input = Map.of(
                Constants.FAL_ASYNC_GENERATE_PREVIEW_PROMPT, prompt
        );

        var future = falClient.subscribe(
                Constants.FAL_ASYNC_GENERATE_PREVIEW_URL,
                SubscribeOptions.<JsonObject>builder()
                        .input(input)
                        .resultType(JsonObject.class)
                        .onQueueUpdate(update -> {
                            System.out.println(update.getStatus());
                        })
                        .build()
        );

        return future.thenApply((Output<JsonObject> result) -> {
            JsonObject output = result.getData().getAsJsonObject(Constants.FAL_ASYNC_GENERATE_PREVIEW_RESPONSE_NAME);
            return output.get(Constants.FAL_ASYNC_GENERATE_PREVIEW_RESPONSE_URL_NAME).getAsString();
        });
    }

    CompletableFuture<List<String>> asyncGenerateAiInfu(String prompt) {
        Map<String, Object> input = Map.of(
                Constants.FAL_ASYNC_GENERATE_AI_INFU_PROMPT, prompt
        );

        var future = falClient.subscribe(
                Constants.FAL_ASYNC_GENERATE_AI_INFU_URL,
                SubscribeOptions.<JsonObject>builder()
                        .input(input)
                        .resultType(JsonObject.class)
                        .onQueueUpdate(update -> {
                            System.out.println(update.getStatus());
                        })
                        .build()
        );

        return future.thenApply((Output<JsonObject> result) -> {
            System.out.println(result);
            JsonObject outputData = result.getData();
            System.out.println(outputData);
            JsonArray images = outputData.getAsJsonArray(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_NAME);
            List<String> urls = new ArrayList<>();

            for (JsonElement elem : images) {
                JsonObject imageObj = elem.getAsJsonObject();
                if (imageObj.has(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_URL_NAME) && !imageObj.get(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_URL_NAME).isJsonNull()) {
                    urls.add(imageObj.get(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_URL_NAME).getAsString());
                }
            }
            return urls;
        });
    }

    CreatePostApiResponseDto generateAiInfuPostApiCall(String aiInfuUrl, String productUrl, String prompt) {
        CreatePostApiRequestDto request = new CreatePostApiRequestDto(prompt, Arrays.asList(aiInfuUrl, productUrl));

        // Send POST
        ResponseEntity<CreatePostApiResponseDto> response =
                restTemplate.postForEntity(
                        "http://localhost:8087/generate", // FastAPI URL
                        request,
                        CreatePostApiResponseDto.class
                );

        return response.getBody();
    }

    CompletableFuture<List<String>> generateAiInfuPost(String aiInfuUrl, String productUrl, String prompt) {
        System.out.println("Here 1");
        Map<String, Object> input = Map.of(
                "prompt", prompt,
                "image_urls", List.of(aiInfuUrl, productUrl),
                "num_images", 1
        );

        /**
        {
            "images": [
            {
                "url": "https://fal.media/files/zebra/1fhEo-yydoKXMA22LhKYh_e396c4aa2cfd4b74910693e0272994a9.jpg",
                    "width": 1024,
                    "height": 1024,
                    "content_type": "image/jpeg"
            }
  ]
        }
         **/

        System.out.println("Here 2");

        CompletableFuture<Output<JsonObject>> future = falClient.subscribe(
                "workflows/hesitationIsDefeat/onalpscreatepost",
                SubscribeOptions.<JsonObject>builder()
                        .input(input)
                        .resultType(JsonObject.class)
                        .onQueueUpdate(update -> {
                            System.out.println("Queue update: " + update.getStatus());
                        })
                        .build()
        );

        System.out.println("Here 3 - future done? " + future.isDone());

        // Log completion or exception immediately for debugging
        future.whenComplete((res, ex) -> {
            if (ex != null) {
                System.err.println("Subscribe future completed exceptionally:");
                ex.printStackTrace();
            } else {
                System.out.println("Subscribe future completed: " + res);
            }
        });

        // Add a timeout so it fails fast in case FAL never completes
        return future
                .orTimeout(60, TimeUnit.SECONDS) // fail if not complete in 60s
                .thenApply(result -> {
                    System.out.println("thenApply entered with result = " + result);
                    if (result == null) {
                        throw new IllegalStateException("FAL result is null");
                    }

                    JsonObject outputData = result.getData();
                    System.out.println("outputData = " + outputData);
                    if (outputData == null) {
                        return List.<String>of(); // or throw
                    }

                    JsonArray images = outputData.getAsJsonArray(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_NAME);
                    System.out.println("images = " + images);

                    List<String> urls = new ArrayList<>();
                    System.out.println("Here 4");
                    if (images != null) {
                        for (JsonElement elem : images) {
                            JsonObject imageObj = elem.getAsJsonObject();
                            if (imageObj.has(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_URL_NAME)
                                    && !imageObj.get(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_URL_NAME).isJsonNull()) {
                                urls.add(imageObj.get(Constants.FAL_ASYNC_GENERATE_AI_INFU_RESPONSE_URL_NAME).getAsString());
                            }
                        }
                    }
                    System.out.println("Here 5 - urls: " + urls);
                    return urls;
                })
                .exceptionally(ex -> {
                    // Log the exception and return an empty list so callers get a result
                    System.err.println("Error while generating AiInfu post:");
                    ex.printStackTrace();
                    return List.of();
                });
    }
}
