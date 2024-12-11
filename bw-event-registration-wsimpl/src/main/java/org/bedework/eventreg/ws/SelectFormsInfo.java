package org.bedework.eventreg.ws;

import java.util.ArrayList;
import java.util.List;

public class SelectFormsInfo {
  public static class Form {
    private final String name;

    /* disabled, published or unpublished */
    private final String status;

    public Form(final String name,
                final String status) {
      this.name = name;
      this.status = status;
    }

    public String getStatus() {
      return status;
    }

    public String getName() {
      return name;
    }
  }

  private final List<Form> bwforms = new ArrayList<>();

  public void addForm(final String name,
                      final String status) {
    bwforms.add(new Form(name, status));
  }

  public List<Form> getBwforms() {
    return bwforms;
  }
}
