package com.recommender.kafka;


import com.recommender.avro.Movie;
import com.recommender.avro.Rating;
import com.recommender.avro.User;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvroProducerService {

    @Value("${app.kafka.user-topic:user-events}")
    private String userTopic;

    @Value("${app.kafka.movie-topic:movie-events}")
    private String movieTopic;

    @Value("${app.kafka.rating-topic:rating-events}")
    private String ratingTopic;

    private final KafkaTemplate<String, SpecificRecord> template;

    public void sendUserEvent(Long userId, List<String> preferredGenres) {
        User user = User.newBuilder()
                .setUserId(userId)
                .setPreferredGenres(preferredGenres.stream().map(string -> (CharSequence) string).toList())
                .build();

        template.send(userTopic, userId.toString(), user);
    }

    public void sendMovieEvent(Long movieId, String title, List<String> genres){
        Movie movie = Movie.newBuilder()
                .setMovieId(movieId)
                .setTitle(title)
                .setGenres(genres.stream().map(string -> (CharSequence) string).toList())
                .build();
        template.send(movieTopic, movieId.toString(), movie);
    }

    public void sendRatingEvent(Long movieId, Long userId, Double rating){
        Rating ratingAvro = Rating.newBuilder()
                .setMovieId(movieId)
                .setUserId(userId)
                .setRatingValue(rating)
                .build();
        template.send(ratingTopic, userId.toString(), ratingAvro);
    }




}
