/*
 * <copyright>
 *  Copyright 1997-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.se.robot;

import org.cougaar.domain.planning.ldm.plan.PlanElement;

import org.cougaar.util.UnaryPredicate;
import java.io.*;
import java.util.*;
import org.cougaar.lib.planserver.PSP_BaseAdapter;
import org.cougaar.lib.planserver.PlanServiceProvider;
import org.cougaar.lib.planserver.UISubscriber;
import org.cougaar.lib.planserver.HttpInput;
import org.cougaar.lib.planserver.PlanServiceContext;
import org.cougaar.lib.planserver.PlanServiceUtilities;
import org.cougaar.lib.planserver.RuntimePSPException;
import org.cougaar.core.cluster.Subscription;
import org.cougaar.domain.planning.ldm.plan.Task;
import org.cougaar.domain.planning.ldm.plan.NewTask;
import org.cougaar.domain.planning.ldm.plan.Verb;
import org.cougaar.domain.planning.ldm.RootFactory;
import org.cougaar.domain.planning.ldm.plan.NewPrepositionalPhrase;

import org.cougaar.core.cluster.IncrementalSubscription;

/**
 * This PSP is meant for the PC 104 board which controls only a single TiniFlashlight
 */
public class PSP_ControlLight extends PSP_BaseAdapter
  implements PlanServiceProvider, UISubscriber
{
  /** A zero-argument constructor is required for dynamically loaded PSPs,
   *         required by Class.newInstance()
   **/
  public PSP_ControlLight()
  {
    super();
  }

  /**
   * This constructor includes the URL path as arguments
   */
  public PSP_ControlLight( String pkg, String id ) throws RuntimePSPException
  {
    setResourceLocation(pkg, id);
  }

  /**
   * Some PSPs can respond to queries -- URLs that start with "?"
   * I don't respond to queries
   */
  public boolean test(HttpInput query_parameters, PlanServiceContext sc)
  {
    super.initializeTest(); // IF subclass off of PSP_BaseAdapter.java
    return false;  // This PSP is only accessed by direct reference.
  }

  static int count=0;

  /**
   * Called when a HTTP request is made of this PSP.
   * @param out data stream back to the caller.
   * @param query_parameters tell me what to do.
   * @param psc information about the caller.
   * @param psu unused.
   */
  public void execute( PrintStream out,
                       HttpInput query_parameters,
                       PlanServiceContext psc,
                       PlanServiceUtilities psu ) throws Exception
  {
    boolean wantOn=false;
    try {
      String onParam="on";
      String onText="false";
      String verbText="ControlFlashlight";

      System.out.println("PSP_ControlLight called from " + psc.getSessionAddress());

      if( query_parameters.existsParameter(onParam) )
      {
         onText = (String) query_parameters.getFirstParameterToken(onParam, '=');
         System.out.println("Input "+onParam+" parm for onText: ["+onText+"]");
         wantOn=onText.equalsIgnoreCase("true");
         System.out.println("wanton is "+wantOn);
      }

      if (wantOn) {
        RootFactory theLDMF = psc.getServerPlugInSupport().getFactoryForPSP();
        Task task=createTask(theLDMF, verbText);
        psc.getServerPlugInSupport().publishAddForSubscriber(task);
        count++;
        createOutputPage("Added", task, out);
      } else {
        IncrementalSubscription subscription = null;
        final String myVerbText=verbText;
        UnaryPredicate taskPred = new UnaryPredicate() {
          public boolean execute(Object o) {
            boolean ret=false;
            if (o instanceof Task) {
              Task mt = (Task)o;
              ret= (mt.getVerb().equals(myVerbText));
            }
            return ret;
          }
        };

        subscription = (IncrementalSubscription)psc
          .getServerPlugInSupport().subscribe(this, taskPred);
        Iterator iter = subscription.getCollection().iterator();
        if (iter.hasNext()) {
          Task task=null;
          int removedCount=0;
          while (iter.hasNext()) {
            task = (Task)iter.next();
            psc.getServerPlugInSupport().publishRemoveForSubscriber(task);
            count--;
            removedCount++;
          }
          createOutputPage("Removed "+removedCount+" ", task, out);
        } else {
          createOutputPage("No More Tasks to be removed with "+verbText+" verb", null, out);
        }
      }
    } catch (Exception ex) {
      out.println(ex.getMessage());
      ex.printStackTrace(out);
      System.out.println(ex);
      out.flush();
    }
  }

  private Task createTask(RootFactory theLDMF, String verbText) {
    NewTask t = theLDMF.newTask();
    t.setPlan(theLDMF.getRealityPlan());
    t.setVerb(Verb.getVerb(verbText));
    return t;
  }

  /**
   * Print HTML that shows info about the task
   */
  private void createOutputPage(String addOrRem, Task task, PrintStream out) {
      // dump classnames and count to output stream
      out.println("<html><head></head><body>");
      if (task!=null) {
        out.println(addOrRem+" Task(s): "+task.getVerb()+"<br>");
      } else {
        out.println(addOrRem+"<br>");
      }
      out.println("Time: "+new Date()+"<br>");
      out.println("Count: "+count+"<br>");
      out.println("</body></html>");
      out.flush();
  }

  /**
   * A PSP can output either HTML or XML (for now).  The server
   * should be able to ask and find out what type it is.
   **/
  public boolean returnsXML() {
    return false;
  }

  public boolean returnsHTML() {
    return true;
  }

  /**  Any PlanServiceProvider must be able to provide DTD of its
   *  output IFF it is an XML PSP... ie.  returnsXML() == true;
   *  or return null
   **/
  public String getDTD()  {
    return null;
  }

  /**
   * The UISubscriber interface. (not needed)
   */
  public void subscriptionChanged(Subscription subscription) {
  }
}

