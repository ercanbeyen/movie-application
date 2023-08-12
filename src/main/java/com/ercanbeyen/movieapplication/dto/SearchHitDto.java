package com.ercanbeyen.movieapplication.dto;

import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Explanation;
import org.springframework.data.elasticsearch.core.document.NestedMetaData;

import java.util.List;
import java.util.Map;

@Data
public class SearchHitDto<T, V> {
    private String index;
    private String id;
    private float score;
    private List<Object> sortValues;
    private T content;
    private Map<String, List<String>> highlightFields;
    private Map<String, SearchHits<?>> innerHits;
    private NestedMetaData nestedMetaData;
    private String routing;
    private Explanation explanation;
    private List<String> matchedQueries;

    public SearchHitDto(SearchHit<V> searchHit, T content) {
        this.index = searchHit.getIndex();
        this.id = searchHit.getId();
        this.score = searchHit.getScore();
        this.sortValues = searchHit.getSortValues();
        this.content = content;
        this.highlightFields = searchHit.getHighlightFields();
        this.innerHits = searchHit.getInnerHits();
        this.nestedMetaData = searchHit.getNestedMetaData();
        this.routing = searchHit.getRouting();
        this.explanation = searchHit.getExplanation();
        this.matchedQueries = searchHit.getMatchedQueries();
    }


}
