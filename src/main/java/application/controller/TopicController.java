package application.controller;

import application.models.dtos.requests.TopicRequest;
import application.models.entities.Vocabulary;
import application.services.imples.TopicServiceImple;
import core.base.PageResponse;
import core.base.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topics")
@Tag(name = "Topic Controller", description = "Manage vocabulary topics")
public class TopicController {
    private final TopicServiceImple topicService;

    @Operation(
            summary = "Create a new Topic",
            description = "Accepts data from the request body, validates it and saves to the database."
    )
    @PostMapping
    public TopicRequest create(@Valid @RequestBody TopicRequest request) {
        return topicService.create(request);
    }

    @Operation(
            summary = "Update a Topic by ID",
            description = "Updates an existing topic. Sensitive fields left blank will retain their current values."
    )
    @PutMapping("/{id}")
    public TopicRequest update(
            @Parameter(description = "ID of the topic to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request
    ) {
        request.setId(id);
        return topicService.update(request);
    }

    @Operation(
            summary = "Get Topic details by ID",
            description = "Retrieves detailed information of a Topic as a DTO based on the provided ID."
    )
    @GetMapping("/{id}")
    public TopicRequest getById(
            @Parameter(description = "ID of the topic to retrieve", example = "1")
            @PathVariable Long id
    ) {
        return topicService.getById(id);
    }

    @Operation(
            summary = "Advanced search & pagination",
            description = "Supports dynamic search with multiple filter conditions, sorting and pagination."
    )
    @PostMapping("/search")
    public PageResponse<TopicRequest> search(@RequestBody SearchRequest request) {
        return topicService.search(request);
    }

    @Operation(
            summary = "Soft delete a Topic",
            description = "Sets isDeleted to true instead of permanently removing from the database."
    )
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID of the topic to delete", example = "1")
            @PathVariable Long id
    ) {
        topicService.delete(id);
    }

    @Operation(
            summary = "Generate vocabularies using Gemini AI",
            description = "Uses Gemini AI to generate a list of vocabularies for a given topic and saves them to the database."
    )
    @PostMapping("/generateVocabularies")
    public Set<Vocabulary> generateVocabularies(@RequestBody TopicRequest request) {
        return topicService.generateVocabulariesByGeminiAndSaveWithTopic(request);
    }
}