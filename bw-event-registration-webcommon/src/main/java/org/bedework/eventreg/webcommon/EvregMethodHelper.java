package org.bedework.eventreg.webcommon;

import org.bedework.eventreg.common.BwConnector;
import org.bedework.eventreg.common.Event;
import org.bedework.eventreg.common.EventregException;
import org.bedework.eventreg.common.Registration;
import org.bedework.eventreg.db.Change;
import org.bedework.eventreg.db.EventregDb;
import org.bedework.eventreg.db.FormDef;
import org.bedework.eventreg.service.EventregNotifier;
import org.bedework.util.servlet.MethodHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import static java.lang.String.format;

public abstract class EvregMethodHelper extends MethodHelper {
  protected EventregDb db;

  /* =====================================================
   *                form fields
   */

  /** Request parameter - comment
   */
  public static final String reqparComment = "comment";

  public static final String reqparCSVHeader = "csvheader";

  public static final String reqparDefault = "default";

  /** Request parameter - comment
   */
  public static final String reqparDescription = "description";

  public static final String reqparDisable = "disable";

  public static final String reqparEmail = "email";

  private static final String reqparFormName = "formName";

  private static final String reqparGroup = "group";

  public static final String reqparHeight = "height";

  public static final String reqparLabel = "label";

  public static final String reqparLastmod = "lastmod";

  public static final String reqparMulti = "multi";

  private static final String reqparName = "name";

  public static final String reqparNumtickets = "numtickets";

  public static final String reqparOptions = "options";

  public static final String reqparOrder = "order";

  public static final String reqparRegId = "regid";

  public static final String reqparRequired = "req";

  public static final String reqparType = "type";

  public static final String reqparValue = "value";

  public static final String reqparWidth = "width";

  protected EvregMethodBase emb;
  protected WebGlobals globals;

  public abstract void evProcess(List<String> resourceUri,
                                 HttpServletRequest req,
                                 HttpServletResponse resp);
  @Override
  public void process(final List<String> resourceUri,
                      final HttpServletRequest req,
                      final HttpServletResponse resp) {
    emb = (EvregMethodBase)super.getMethodBase();
    globals = emb.getWebGlobals();
    evProcess(resourceUri, req, resp);
  }

  protected EventregDb getEventregDb() {
    if (db != null) {
      return db;
    }

    synchronized (this) {
      if (db != null) {
        return db;
      }
      db = new EventregDb();
      db.setSysInfo(emb.getConfig());
    }

    return db;
  }

  protected EvregMethodBase getMethodBase() {
    return emb;
  }

  protected Request getRequest() {
    return emb.getRequest();
  }

  public boolean requireCalsuite() {
    if (globals.getCalsuite() == null) {
      errorReturn("Bad resource url - no calsuite specified");
      return false;
    }
    return true;
  }

  public boolean requireFormName() {
    if (globals.getFormName() == null) {
      errorReturn("Bad resource url - no form name specified");
      return false;
    }
    return true;
  }

  public boolean requireCurrentEvent() {
    if (globals.getCurrentEvent() == null) {
      errorReturn("No current event available");
      return false;
    }
    return true;
  }

  public boolean requireHref() {
    if (globals.getHref() == null) {
      errorReturn("Bad resource url - no href specified");
      return false;
    }
    return true;
  }

  /**
   * @return par or null for no parameter
   */
  public boolean groupPresent() {
    return getReqUtil().getReqPar(reqparGroup) != null;
  }

  /**
   * @return par or null for no parameter
   */
  public String reqGroup() {
    return emb.validName(getReqUtil().getReqPar(reqparGroup));
  }

  /**
   * @return comment or null for no parameter
   */
  public String reqComment() {
    return getReqUtil().getReqPar(reqparComment);
  }

  /**
   * @return true if header required
   */
  public boolean reqCsvHeader() {
    return getReqUtil().getBooleanReqPar(reqparCSVHeader, true);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqDescription() {
    return getReqUtil().getReqPar(reqparDescription);
  }

  /**
   * @return email or null for no parameter
   */
  public String reqEmail() {
    return getReqUtil().getReqPar(reqparEmail);
  }

  /**
   * @return lastmod or null for no parameter
   */
  public String reqLastmod() {
    return getReqUtil().getReqPar(reqparLastmod);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqName() {
    return emb.validName(getReqUtil().getReqPar("name"));
  }

  /**
   * @return ticket id or null for no parameter
   */
  public Long reqRegistrationId() {
    return getReqUtil().getLongReqPar(reqparRegId, -1);
  }

  /**
   * @return number of tickets or -1 for no parameter
   */
  public int reqTicketsRequested() {
    return getReqUtil().getIntReqPar(reqparNumtickets, -1);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqType() {
    return getReqUtil().getReqPar("type");
  }

  /**
   * @return par or null for no parameter
   */
  public String reqValue() {
    return getReqUtil().getReqPar(reqparValue);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqWidth() {
    return getReqUtil().getReqPar(reqparWidth);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqHeight() {
    return getReqUtil().getReqPar(reqparHeight);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqLabel() {
    return getReqUtil().getReqPar(reqparLabel);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean reqMulti() {
    return getReqUtil().getBooleanReqPar(reqparMulti, false);
  }

  /**
   * @return par or null for no parameter
   */
  public int reqOrder() {
    return getReqUtil().getIntReqPar(reqparOrder, 0);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean reqRequired() {
    return getReqUtil().getBooleanReqPar(reqparRequired, false);
  }

  /**
   * @return par or null for no parameter
   */
  public boolean reqDefault() {
    return getReqUtil().getBooleanReqPar(reqparDefault, false);
  }

  /**
   * @return par or null for no parameter
   */
  public String reqOptions() {
    return getReqUtil().getReqPar(reqparOptions);
  }

  protected void checkFormOwner(final FormDef form) {
    if (!form.getOwner().equals(globals.getCalsuite())) {
      throw new EventregException("Owner not current calsuite");
    }
  }

  public FormDef getCalSuiteForm() {
    if (!requireCalsuite() || !requireFormName()) {
      return null;
    }

    final var calsuite = globals.getCalsuite();
    final var formName = globals.getFormName();

    final var form =
            getEventregDb().getCalSuiteForm(formName,
                                            calsuite);

    if (form == null) {
      errorReturn(
              format("Form %s not found for calsuite %s",
                     formName, calsuite));
      return null;
    }

    return form;
  }

  /** Adjust tickets for the current event - perhaps as a result of increasing
   * seats.
   */
  protected void adjustTickets(final EventregDb db) {
    final Event currEvent = globals.getCurrentEvent();

    final long allocated = db.getRegTicketCount(globals.getHref());
    final int total = currEvent.getMaxTickets();
    final int available = (int)(total - allocated);

    if (available <= 0) {
      return;
    }

    reallocate(db, available, globals.getHref());
  }

  protected enum AdjustResult {
    removed,
    added,
    nochange,     // No change to registration
    notickets,    // Not allocating tickets
    waitListFull  // Wait list is full
  }

  protected AdjustResult adjustTickets(final EventregDb db,
                                       final Registration reg,
                                       final boolean admin) {
    final var currEvent = globals.getCurrentEvent();
    final var href = globals.getHref();

    final int numTickets = reqTicketsRequested();
    if (numTickets < 0) {
      // Not setting tickets
      return AdjustResult.notickets;
    }

    /* change > 0 to add tickets, < 0 to release tickets
     * For a new registration  reg.getTicketsRequested() == 0
     * */
    int change = numTickets - reg.getTicketsRequested();

    if (change == 0) {
      // Not changing anything
      return AdjustResult.nochange;
    }

    /* For a non-admin user limit the change to the number available */
    if (!admin && (change > 0)) {
      /* First see if wait list is full
       */
      String waitListLimitVal = currEvent.getWaitListLimit();
      final int total = currEvent.getMaxTickets();

      final long allocated =
              db.getRegTicketCount(href);

      /* Total number of available tickets - may be negative for over-allocated */
      final long available = Math.max(0, total - allocated);

      if (waitListLimitVal != null) {
        final boolean percentage = waitListLimitVal.endsWith("%");
        if (percentage) {
          waitListLimitVal =
                  waitListLimitVal.substring(0,
                                             waitListLimitVal.length() - 1);
        }

        int waitListLimit = Integer.parseInt(waitListLimitVal.trim());
        if (percentage) {
          waitListLimit = total * waitListLimit / 100;
        }

        final long waiting = db.getWaitingTicketCount(href);
        if ((available < change) && ((waiting + change - available) > waitListLimit)) {
          return AdjustResult.waitListFull;
        }
      }

      /* The number to add to this registration */
      final int toAllocate = (int)Math.min(change, available);

      if (toAllocate != change) {
        /* We should check the request par expectedAvailable to see if it changed
         * during this interaction. If it did we may have given the user stale
         * information.
         *
         * If so abandon this and represent the information to the user.
         */
      }

      change = toAllocate;
    }

    if ((reg.getWaitqDate() == null) ||
            (change > 0)) {
      reg.setWaitqDate();
    }

    reg.setTicketsRequested(numTickets);

    if (change < 0) {
      reg.removeTickets(-change);
      addChange(db, reg, Change.typeUpdReg,
                       ChangeItem.makeUpdNumTickets(change));
      reallocate(db, -change, reg.getHref());

      return AdjustResult.removed;
    }

    reg.addTickets(change);

    addChange(db, reg, Change.typeUpdReg,
                     ChangeItem.makeUpdNumTickets(change));
    return AdjustResult.added;
  }

  protected void updateRegistration(final EventregDb db,
                                    final boolean admin) {
    final Long regId = reqRegistrationId();
    if (regId == null) {
      errorReturn("No registration id supplied");
      return;
    }

    if (debug()) {
      debug("updating registration " + regId);
    }

    final var reg = db.getByid(regId);

    if (reg == null) {
      errorReturn("No registration found.");
      return;
    }

    if (!admin &&
            !reg.getAuthid().equals(globals.getCurrentUser())) {
      errorReturn("You are not authorized to update that registration.");
      return;
    }

    adjustTickets(db);

    reg.setComment(reqComment());
    reg.setLastmod();
    db.update(reg);
    EventregNotifier.notify(emb.getConfig(),
                            reg.getHref());

    globals.setMessage(format(
            "Registration number %s updated: user: %s", regId, globals.getCurrentUser()));
  }

  protected void removeRegistration(final EventregDb db,
                                    final boolean admin) {
    final Long regId = reqRegistrationId();
    if (regId == null) {
      errorReturn("No registration id supplied");
      return;
    }

    if (debug()) {
      debug("remove reg id: " + regId +
                    ", user: " + globals.getCurrentUser());
    }

    final var reg = db.getByid(regId);

    if (reg == null) {
      errorReturn("No registration found.");
      return;
    }

    if (!admin &&
            !reg.getAuthid().equals(globals.getCurrentUser())) {
      errorReturn("You are not authorized to remove that registration.");
      return;
    }

    reallocate(db, reg.getNumTickets(), reg.getHref());
    db.delete(reg);
    addChange(db, reg, Change.typeDelReg);
  }

  /** Allocate given number of tickets for the event to any waiters
   *
   * @param numTickets number of tickets
   * @param href of event
   */
  protected void reallocate(final EventregDb db,
                            final int numTickets,
                            final String href) {
    final var regs = db.getWaiting(href);

    int remaining = numTickets;

    for (final var reg: regs) {
      int required = reg.getTicketsRequested() - reg.getNumTickets();

      if (required <= 0) {
        continue;
      }

      required = Math.min(required, remaining);

      reg.addTickets(required);
      db.update(reg);
      addTicketsAdded(db, reg, required);

      if (reg.getTicketsRequested() == reg.getNumTickets()) {
        addChange(db, reg, Change.typeRegFulfilled);
      }

      remaining -= required;

      if (remaining <= 0) {
        break;
      }
    }
  }

  /**
   * @param reg changed Registration
   * @param numTickets added
   */
  public void addTicketsAdded(final EventregDb db,
                              final Registration reg,
                              final int numTickets) {
    addChange(db, reg, Change.typeTktAdded,
              ChangeItem.makeUpdNumTickets(reg.getNumTickets()));
  }

  /**
   * @param reg changed Registration
   * @param type of change
   * @param ci change item
   */
  public void addChange(final EventregDb db,
                        final Registration reg,
                        final String type,
                        final ChangeItem ci) {
    final Change c = new Change();

    c.setAuthid(globals.getCurrentUser());
    c.setRegistrationId(reg.getRegistrationId());
    c.setLastmod(reg.getLastmod());
    c.setType(type);
    c.setName(ci.getName());
    c.setValue(ci.getValue());

    db.addChange(c);
  }

  /**
   * @param reg changed Registration
   * @param type of change
   */
  public void addChange(final EventregDb db,
                        final Registration reg,
                        final String type) {
    final Change c = new Change();

    c.setAuthid(globals.getCurrentUser());
    c.setRegistrationId(reg.getRegistrationId());
    c.setLastmod(reg.getLastmod());
    c.setType(type);

    db.addChange(c);
  }

  protected BwConnector getConnector() {
    return emb.getConnector();
  }

  protected void errorReturn(final Throwable t) {
    globals.setMessage(t.getLocalizedMessage());
    if (debug()) {
      debug("Exception- error return " + t.getLocalizedMessage());
    }
    forward("error");
  }

  protected void errorReturn(final String msg) {
    globals.setMessage(msg);
    if (debug()) {
      debug("Error return " + msg);
    }
    forward("error");
  }
}
