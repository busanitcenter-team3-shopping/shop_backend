package com.secure.shopbackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Category {

  ALL("전체", "📄"),
  IT("IT", "⚙️"),
  CLOTHING("의류", "👕"),
  STATIONERY("문구", "📜"),
  INSTRUMENT("악기", "🎻");

  private final String name;
  private final String icon;

  Category(String name, String icon) {
    this.name = name;
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }
}
