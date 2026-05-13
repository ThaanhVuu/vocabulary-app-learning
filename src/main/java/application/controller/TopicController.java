package application.controller;

import application.models.dtos.requests.TopicRequest;
import application.models.dtos.requests.VocabularyDTO;
import application.models.entities.Vocabulary;
import application.services.imples.TopicServiceImple;
import core.base.PageResponse;
import core.base.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topics")
@Tag(name = "Topic Controller", description = "Manage vocabulary topics and their vocabularies")
public class TopicController {

    private final TopicServiceImple topicService;

    // --- CÁC THAO TÁC CƠ BẢN (CRUD) ---

    @Operation(summary = "Create a new Topic")
    @PostMapping
    public TopicRequest create(@Valid @RequestBody TopicRequest request) {
        return topicService.create(request);
    }

    @Operation(summary = "Update a Topic by ID")
    @PutMapping("/{id}")
    public TopicRequest update(@PathVariable Long id, @Valid @RequestBody TopicRequest request) {
        request.setId(id);
        return topicService.update(request);
    }

    @Operation(summary = "Get Topic details by ID")
    @GetMapping("/{id}")
    public TopicRequest getById(@PathVariable Long id) {
        return topicService.getById(id);
    }

    @Operation(summary = "Advanced search & pagination")
    @PostMapping("/search")
    public PageResponse<TopicRequest> search(@RequestBody SearchRequest request) {
        return topicService.search(request);
    }

    @Operation(summary = "Soft delete a Topic")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        topicService.delete(id);
    }

    @Operation(
            summary = "Generate vocabularies using Gemini AI",
            description = "Automatically generates 20 vocabularies for the topic and saves them."
    )
    @PostMapping("/generate-vocabularies")
    public Set<Vocabulary> generateVocabularies(@RequestBody TopicRequest request) {
        return topicService.generateVocabulariesByGeminiAndSaveWithTopic(request);
    }

    @Operation(
            summary = "Update multiple vocabularies",
            description = "Updates content of existing vocabularies (by english key) or adds new ones (if key is not existed)."
    )
    @PutMapping("/{id}/vocabularies")
    public TopicRequest updateVocabularies(
            @PathVariable Long id,
            @RequestBody Set<VocabularyDTO> vocabDtos) {
        return topicService.updateVocabs(id, vocabDtos);
    }

    @Operation(
            summary = "Remove multiple vocabularies",
            description = "Removes vocabularies from topic by their English words."
    )
    @DeleteMapping("/{id}/vocabularies")
    public TopicRequest removeVocabularies(
            @PathVariable Long id,
            @RequestBody Set<String> englishWords) {
        return topicService.removeVocabs(id, englishWords);
    }
}