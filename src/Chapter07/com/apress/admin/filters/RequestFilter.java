/*
 * Copyright 2004 Matthew Moodie.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apress.admin.filters;

import java.io.IOException;

import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of a filter that performs filtering based on comparing the
 * appropriate request property against a set of regular
 * expressions configured for this filter.
 * <p>
 * This filter is configured by setting the <code>allow</code> and/or
 * <code>deny</code> filter initialization parameters to a comma-delimited 
 * list of regular expressions (in the syntax supported by the jakarta-regexp 
 * library) in <code>web.xml</code> to which the appropriate request property 
 * will be compared.  Evaluation proceeds as follows:
 * <ul>
 * <li>The filter extracts the request property to be filtered.
 * <li>If there are any deny expressions configured, the property will
 *     be compared to each such expression.  If a match is found, this
 *     request will be forwarded to a blocked page (configured with the
 *     <code>blockPage</code> filter initialization parameter in 
 *     <code>web.xml</code>).</li>
 * <li>If there are any allow expressions configured, the property will
 *     be compared to each such expression.  If a match is found, this
 *     request will be allowed to pass through to the next filter in the
 *     current pipeline.</li>
 * <li>If one or more deny expressions was specified but no allow expressions,
 *     allow this request to pass through (because none of the deny
 *     expressions matched it).
 * <li>The request will be forwarded to a blocked page (configured with the
 *     <code>blockPage</code> filter initialization parameter in 
 *     <code>web.xml</code>).</li>
 * </ul>
 * <p>
 * This filter is based on, and is a modification of, the request 
 * interception mechanism provided by Tomcat's remote request 
 * filter valves. Instead of returning a forbidden HTTP response,
 * it forwards the request to a custom blocked page.
 * 
 * In particular, it uses code from <code>org.apache.catalina.valves.RequestFilterValve</code>
 * to process a list of allowed and denied IP addresses and remote hosts, as well
 * as code to check the user's details against this list. The difference in this
 * filter is that it doesn't have implementing classes to differentiate between
 * IP addresses and remote hosts. It simply checks them all at once.
 *
 * @author Matthew Moodie
 * @version $Revision: 1.0 $ $Date: 2004/08/04 23:07:00 $
 */

public class RequestFilter implements Filter {

    // ----------------------------------------------------- Instance Variables


    /**
     * The comma-delimited set of <code>allow</code> expressions.
     */
    protected String allowedList = null;

    /**
     * The set of <code>allow</code> regular expressions we will evaluate.
     */
    protected RE allows[] = new RE[0];

    /**
     * The set of <code>deny</code> regular expressions we will evaluate.
     */
    protected RE denies[] = new RE[0];

    /**
     * The comma-delimited set of <code>deny</code> expressions.
     */
    protected String deniedList = null;

    /* The configuration associated with this filter */
    private FilterConfig config =  null;

    /* The page informing users that their request has been blocked */
    private String blockPage = "";

    /* The remote host and IP address of the client */
    private String host = null;
    private String ip = null; 

    /**
     * Initializes the filter at server startup.
     * Initialization consists of setting the list of allowed
     * and disallowed remote hosts and IP addresses.
     * @param _config The configuration associated with this filter
     *
     * @exception ServletException if there was a problem initializing this filter
     */
    public void init(FilterConfig _config)
      throws ServletException {
        this.config = _config;

        /* Get the initialization parameters as a string */
        allowedList = _config.getInitParameter("allow");
        deniedList = _config.getInitParameter("deny");

	blockPage = _config.getInitParameter("blockPage");

        /* Turn the lists into regular expression lists */
        allows = precalculate(allowedList);
        denies = precalculate(deniedList);
    }

    /**
     * Processes a request and blocks it as appropriate.
     * This method takes the list of allowed and disallowed
     * remote hosts and IP addresses and checks if the client's
     * details match them.
     *
     * Some of the code in this method comes from <code>org.apache.catalina.valves.RequestFilterValve</code>.
     *
     * @param _req The request
     * @param _res The response
     * @param _chain The filter chain that this filter belongs to
     *
     * @exception IOException if there is a problem with the filter
     *  chain or the forward
     * @exception ServletException if there is a problem with the filter
     *  chain or the forward
     */
    public void doFilter(ServletRequest _req, ServletResponse _res,
        FilterChain _chain) throws IOException, ServletException {
        /* Get the type */
        if (_req instanceof HttpServletRequest) {
            host = _req.getRemoteHost();
            ip = _req.getRemoteAddr();
        }

        // Check the deny patterns, if any
        for (int i = 0; i < denies.length; i++) {
            if (denies[i].match(host) || denies[i].match(ip)) {
                if (_req instanceof HttpServletRequest) {
	            config.getServletContext().getRequestDispatcher(blockPage).forward(_req, _res);
	            return;
                }
            }
        }

        if(!(_res.isCommitted())){
            // Check the allow patterns, if any
            for (int i = 0; i < allows.length; i++) {
                if (allows[i].match(host) || allows[i].match(ip)) {
		    _chain.doFilter(_req, _res);
		    return;
                }
            }
        }

        if(!(_res.isCommitted())){
            // Allow if denies specified but not allows
            if ((denies.length > 0) && (allows.length == 0)) {
                _chain.doFilter(_req, _res);
                return;
            }
        }

       if(!(_res.isCommitted())){
           // Deny this request
           if (_req instanceof HttpServletRequest) {
	      config.getServletContext().getRequestDispatcher(blockPage).forward(_req, _res);
           } 
       }
    }
  

    /**
     * Return an array of regular expression objects initialized from the
     * specified argument, which must be <code>null</code> or a comma-delimited
     * list of regular expression patterns.
     *
     * This method comes from <code>org.apache.catalina.valves.RequestFilterValve</code>.
     *
     * @param list The comma-separated list of patterns
     *
     * @exception IllegalArgumentException if one of the patterns has
     *  invalid syntax
     */
    protected RE[] precalculate(String list) {

        if (list == null)
            return (new RE[0]);
        list = list.trim();
        if (list.length() < 1)
            return (new RE[0]);
        list += ",";

        ArrayList reList = new ArrayList();
        while (list.length() > 0) {
            int comma = list.indexOf(',');
            if (comma < 0)
                break;
            String pattern = list.substring(0, comma).trim();
            try {
                reList.add(new RE(pattern));
            } catch (RESyntaxException e) {
                IllegalArgumentException iae = new IllegalArgumentException("Illegal pattern: " +  pattern);
                throw iae;
            }
            list = list.substring(comma + 1);
        }

        RE reArray[] = new RE[reList.size()];
        return ((RE[]) reList.toArray(reArray));

    }

    /**
     * Called when the filter is taken out of service at server shutdown.
     *
     */
    public void destroy() {
        config = null;
    }
}
