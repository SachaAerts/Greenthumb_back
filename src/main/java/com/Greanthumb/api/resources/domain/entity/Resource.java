package com.Greanthumb.api.resources.domain.entity;

import java.util.Date;

public record Resource(String title, String light, String urlPicture, String text, Date creationDate) {
}
