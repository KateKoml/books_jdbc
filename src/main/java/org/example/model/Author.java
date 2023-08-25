package org.example.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class Author {
    private Long id;
    private String fullName;
    private Integer yearOfBirth;
    private List<Book> books;

    public Author(){
    }

    public Author(Long id, String fullName, Integer yearOfBirth) {
        this.id = id;
        this.fullName = fullName;
        this.yearOfBirth = yearOfBirth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author author)) return false;

        if (!getId().equals(author.getId())) return false;
        if (!getFullName().equals(author.getFullName())) return false;
        return getYearOfBirth().equals(author.getYearOfBirth());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getFullName().hashCode();
        result = 31 * result + getYearOfBirth().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
