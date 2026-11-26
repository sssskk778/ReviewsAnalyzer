package com.aska.consumer.parser;

import com.aska.consumer.dto.Review;

import java.util.List;

public interface IReviewsParser {

    List<Review> fetchReviews(String productUrl);
}
