package wiki;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * The Hub servlet handles PubSubHubBub subscribe and unsubscribe
 * actions for the wiki.  The URI relative paths handled are:
 *
 *   /hub/feed - callback for feed updates.
 *   /hub/verify - callback for subscribe validation.
 *   /hub/worker - web hook for new subscriptions.
 *
 * The hub to use is configured in the datawiki.pshb system property,
 * or uses the PSHB public hub by default.
 *
 * @author Pablo Mayrgundter
 */
@Path("/hub")
public class Hub {

  static final Logger logger = Logger.getLogger(Hub.class.getName());
  static final String DEFAULT_HUB = "pubsubhubbub.appspot.com/subscribe";
  static final String HUB = System.getProperty("datawiki.pshb", DEFAULT_HUB);
  static final String VERIFY_HANDLER = "/wiki/hub/verify";
  static final String FEED_HANDLER = "/wiki/hub/feed";
  static final String SUBSCRIPTION_WORKER = "/hub/worker";

  public Hub() {
    logger.info("System.getProperty(\"datawiki.pshb\"): "+ HUB);
  }

  /**
   * Processes subscribe and unsubscribe actions initiated by topics
   * in the wiki that are being linked or unlinked to feeds of their
   * type.
   *
   * @param topicUrl The feed topic to subsribe to.
   * @param hubUrl The hub managing the subscription.
   * @return 200 if OK, 400 if bad request.
   */
  @GET
  @Path("/worker")
  @Produces({"text/html"})
  public Response subscription(@Context final HttpServletRequest req,
                               @QueryParam("hub.topic") final String topic,
                               @QueryParam("hub.mode") final String mode) {
    final String hubUrlStr = "http://"+ HUB;
    URL url;
    try {
      url = new URL(hubUrlStr);
    } catch (java.net.MalformedURLException e) {
      return Util.getErrorResponse("Server error constructing subscription.", logger, 500, e);
    }

    final String body =
      Util.encodePost(new String[][]{{"hub.mode", mode},
                                     {"hub.callback", Util.getHostURL(req) + VERIFY_HANDLER},
                                     {"hub.verify", "sync"},
                                     {"hub.topic", topic}});
    logger.info("Subscription request to: "+ hubUrlStr +", with params: "+ body);
    HttpURLConnection conn;
    int rspCode;
    String rspMsg;
    PrintWriter pw;
    final byte [] bodyBytes = body.getBytes();
    try {
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("Content-Length", ""+ bodyBytes.length);
      conn.setDoOutput(true);
      conn.connect();
      conn.getOutputStream().write(bodyBytes);
      conn.getOutputStream().flush();
      rspCode = conn.getResponseCode();
      rspMsg = conn.getResponseMessage();
    } catch (java.io.IOException e) {
      return Util.getErrorResponse("Problem contacting PubSub hub.", logger, 502, e);
    }
    if (rspCode != 204) {
      return Util.getErrorResponse("Problem subscribing at hub (code "+ rspCode +"): "+ rspMsg, logger, 502);
    }
    logger.info("Subscription accepted for topic: "+ topic);
    return Response.ok().build();
  }

  @GET
  @Path("/verify")
  @Produces({"text/html"})
  public Response verify(@Context final HttpServletRequest req,
                         @QueryParam("hub.mode") final String mode,
                         @QueryParam("hub.topic") final String topic,
                         @QueryParam("hub.challenge") final String challenge) {
    logger.info("Subscription verified for topic: "+ topic);
    return Response.ok().entity(challenge).build();
  }

  @GET
  @Path("/feed")
  @Produces({"text/html"})
  public Response feed(@Context final HttpServletRequest req) {
    logger.info("Feed received.");
    return Response.ok().build();
  }

  /**
   * Create feed subscribe and unsubscribe tasks for topics in the
   * wiki that are being linked or unlinked to feeds of their type.
   *
   * @param topicUrl The feed topic to subsribe to.
   * @param hubUrl The hub managing the subscription.
   * @return 200 if OK, 400 if bad request.
   */
  public void enqueuePubSubAction(final String topic, final String mode) {

    if (topic == null) {
      throw new NullPointerException("topic must not be null.");
    }

    if (mode == null || !mode.equals("subscribe") || !mode.equals("unsubscribe")) {
      throw new IllegalArgumentException("mode must be subscribe or unsubscribe.");
    }

    final Queue queue = QueueFactory.getDefaultQueue();
    TaskOptions task = url(SUBSCRIPTION_WORKER);
    task.param("hub.topic", topic);
    task.param("hub.mode", mode);
    queue.add(task);
    logger.info("Subscription enqueued.");
  }
}
