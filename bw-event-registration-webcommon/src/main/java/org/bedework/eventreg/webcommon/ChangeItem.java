package org.bedework.eventreg.webcommon;

import org.bedework.eventreg.db.Change;

/** Specify the change by label and value
 */
public class ChangeItem {
  private String name;
  private String value;

  /**
   * @param name of changed value
   * @param value changed value
   */
  public ChangeItem(final String name,
                    final String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * @param value int new value
   * @return new change item
   */
  public static ChangeItem makeUpdNumTickets(final int value) {
    return new ChangeItem(Change.lblUpdNumTickets,
                          String.valueOf(value));
  }

  /**
   * @param val name of changed value
   */
  public void setName(final String val) {
    name = val;
  }

  /**
   * @return name of changed value
   */
  public String getName() {
    return name;
  }

  /**
   * @param val changed value
   */
  public void setValue(final String val) {
    value = val;
  }

  /**
   * @return changed value
   */
  public String getValue() {
    return value;
  }
}
