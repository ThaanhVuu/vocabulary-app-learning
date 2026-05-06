package application.services;

import application.models.dtos.requests.TopicRequest;
import application.models.entities.Vocabulary;

import java.util.Set;

public interface TopicService{
    Set<Vocabulary> generateVocabulariesByGeminiAndSaveWithTopic(TopicRequest request);
}
