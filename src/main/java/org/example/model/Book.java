package org.example.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class Book {
    private Long id;
    private String name;
    private Integer year;
    private Long authorId;
    private List<Genre> genres;

    public Book(){
    }

    public Book(String name, Integer year, Long authorId) {
        this.name = name;
        this.year = year;
        this.authorId = authorId;
    }

    public Book(Long id, String name, Integer year, Long authorId) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;

        if (!getId().equals(book.getId())) return false;
        if (!getName().equals(book.getName())) return false;
        if (!getYear().equals(book.getYear())) return false;
        return getAuthorId().equals(book.getAuthorId());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getYear().hashCode();
        result = 31 * result + getAuthorId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
