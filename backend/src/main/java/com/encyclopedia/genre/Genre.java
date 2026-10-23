package com.encyclopedia.genre;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "name_uk", nullable = false)
    private String nameUk;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    // JSON array of infobox rows: [{ "key": "...", "uk": "...", "en": "..." }]
    @Column(columnDefinition = "text")
    private String infobox;

    protected Genre() {
    }

    public Genre(String slug, String nameUk, String nameEn, String infobox) {
        this.slug = slug;
        this.nameUk = nameUk;
        this.nameEn = nameEn;
        this.infobox = infobox;
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNameUk() {
        return nameUk;
    }

    public void setNameUk(String nameUk) {
        this.nameUk = nameUk;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getInfobox() {
        return infobox;
    }

    public void setInfobox(String infobox) {
        this.infobox = infobox;
    }
}
