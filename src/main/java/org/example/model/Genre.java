package org.example.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Genre {
    private Integer id;
    private String type;

    public Genre(){
    }

    public Genre(Integer id, String type) {
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre genre)) return false;

        if (!getId().equals(genre.getId())) return false;
        return getType().equals(genre.getType());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
