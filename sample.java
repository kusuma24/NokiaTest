
package com.alu.sam.nbapi.syncnb;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.xml.parsers.ParserConfigurationException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.databinding.ADBException;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAbstractFactory;

import com.alu.sam.nbapi.syncnb.axis2gen.SAMSyncServiceSkeletonInterface;
import com.alu.sam.nbapi.syncnb.axis2gen.CreateWorkItemResponse;
import com.alu.sam.nbapi.syncnb.axis2gen.CreateWorkItemResponseE;
import com.alu.sam.nbapi.syncnb.axis2gen.Param;
import com.alu.sam.nbapi.syncnb.axis2gen.SAMSoapWorkItemRequest;
import com.alu.sam.common.api.common.IdcmErrMsgException;
import com.alu.sam.common.api.common.IdcmFramework;
import com.alu.sam.common.api.common.IdcmTracer;
import com.alu.sam.common.api.common.IdcmConfiguration;
import com.alu.sam.common.api.common.CryptUtil;
import com.alu.sam.common.api.common.Codec;
import com.alu.sam.common.api.data.WorkItemRequest;
import com.alu.sam.nbapi.syncnb.VSAMSoap2XMLDataConverter;
import com.alu.sam.nbapi.notify2.NotifySoapDataConverter;
import com.alu.sam.common.servers.common.ConnectionPool;
import com.alu.sam.common.servers.oteintf.GetWorkItemConfiguration;
import com.alu.sam.common.servers.common.SearchDataUtil;
import com.alu.sam.common.servers.common.NBActivityLogUtil;

import com.alu.sam.common.servers.common.*;
import com.alu.sam.common.servers.common.CreateWorkItemUtils;

import java.sql.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import org.w3c.dom.Element;

/**
 * SAMSyncServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:47 LKT)
 */

/**
 * SAMSyncServiceSkeleton java skeleton for the axisService
 */
public class SAMSyncServiceSkeleton implements SAMSyncServiceSkeletonInterface {
	private static final String className = "SAMSyncServiceSkeleton";
	public static final String APPNAME = "SyncNBModule";

	private static String syncWorkItemType = "API";
	private static String syncWorkItemPrefix = "SO";
	private static int syncWorkItemDataType = 2;
	private static String Priority = "Priority";
	private static String REQUESTSTATUS = "REQUESTSTATUS";
	public static int threadPerProcess = 1;
	public static int pethrIdx = 0;
	public static int peprocIdx = 0;
	public static int noOfPEProcess = 1;
	public static String asyncResponseRequired = "NO";
	public static int btapicnt = 10;
	public static int syncThreads = 0;
	public static String pePrefix = "OteMgr";
	public static ArrayList freeSvc = new ArrayList();
	public static Object syncSvc = new Object();
	public static Object syncJMS = new Object();
	private static boolean initJmsFlag = false;
	private static final String APPTAG_NAME = "APPLICATION_TAG";
	private static final String REQUESTAPPLTAG = "reqApplTag";

	public static Map jQueues = Collections.synchronizedMap(new HashMap());
	static com.alu.sam.nbapi.notify2.axis2gen.Param param = null;
	public final static String GCL_PARAM = "operationList[0].GCL";
	public final static String DATAMAP_TYPE = "DATAMAP";
	public static HashMap featureLockDataMap = new HashMap();
	public static HashMap<String, Integer> wiCountMap = null;

	public static Timer RefreshCacheTaskTimer = null;
	public static RefreshCacheTask RefreshCacheTask1 = null;

	private static String hostName;

	static {
		try {
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException ex) {
			hostName = "";
		}
	}

	static {
		try {
			init();
		} catch (IdcmErrMsgException e) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, "Static block",
					"Error loading SAMSyncServiceSkeleton Init: ", IdcmTracer.getExceptionTraceAsString(e));
		}
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param createWorkItem
	 */

	public com.alu.sam.nbapi.syncnb.axis2gen.CreateWorkItemResponseE CreateWorkItem(
			com.alu.sam.nbapi.syncnb.axis2gen.CreateWorkItem createWorkItem)
					throws com.alu.sam.nbapi.syncnb.axis2gen.MessageFault {

		final String methodName = "createWorkItem()";
		IdcmTracer.traceEntry(className, methodName, "");

		boolean isError = false;
		String wkId = "";
		String status = "";
		String preStat = null;
		boolean isPreProcessErr = false;
		boolean cntDcr = false;
		String workItemId = "";

		WorkItemRequest wmWir = new WorkItemRequest();
		WorkItemRequest sentWir = new WorkItemRequest();
		String soapBody = null;
		String envString = null;
		String soapHeader = null;
		long maxProcessTimeOut = 0;
		long startCurrMilliSec = 0;
		long balanceTimeOut = 0;
		String svcName = "OteMgr00";
		long skelParse = System.currentTimeMillis();
		String Remarks = "";
		String wiStatus = null;

		long aftercache = 0;
		long aftercacheupdate = 0;

		com.alu.sam.nbapi.notify2.ShareMem instance1 = com.alu.sam.nbapi.notify2.ShareMem.getShareMem();
		CountDownLatch startSignal = new CountDownLatch(1);
		HashMap wkShmMap = new HashMap();

		CreateWorkItemResponseE createResponse = new CreateWorkItemResponseE();
		CreateWorkItemResponse response = new CreateWorkItemResponse();
		MessageContext msgContext = MessageContext.getCurrentMessageContext();

		String userId = (String) msgContext.getProperty("USERID");
		UserData.incrSessionLimit(userId);

		preStat = (String) msgContext.getProperty("REQUESTSTATUS");

		if (preStat != null) {
			isPreProcessErr = true;
			isError = true;
		}

		try {
			maxProcessTimeOut = Long.parseLong((String) msgContext.getProperty("MAXPROCESSINGTIMEOUT"));
		} catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
					"Exception extracting timeout values from Context:" + ex.toString());
		}

		try {
			startCurrMilliSec = Long.parseLong((String) msgContext.getProperty("STARTTIME"));
		} catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
					"Exception extracting timeout:" + ex.toString());
		}

		// Go in queue, till the thread get free
		if (!isError) {
			CountDownLatch QSignal = new CountDownLatch(1);
			QueueObject.addItemToGeneralQueue(QSignal, userId);

			try {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Waiting for Priority scheduling");
				boolean isLatchTimeout = QSignal.await(
						(maxProcessTimeOut - (System.currentTimeMillis() - startCurrMilliSec)), TimeUnit.MILLISECONDS);
				if (isLatchTimeout == false) {
					preStat = "ProcessingTimeOut";
					IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
							"Processing TimedOut in priority scheduling");
					isPreProcessErr = true;
					isError = true;
					msgContext.setProperty(REQUESTSTATUS, preStat);
				}
			} catch (InterruptedException Ex) {
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
						"Latch Timeout is interrupted" + Ex.toString());
			}
		}

		long outQ = System.currentTimeMillis();
		HashMap map = new HashMap();
		String reqSource = (String) msgContext.getProperty("REQSOURCE");

		if (!isError) {
			try {
				SOAPEnvelope sEnvelope = msgContext.getEnvelope();
				try {
					envString = sEnvelope.toString();
				} catch (Exception e) {
					IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
							" Exception at sEnvelope.toString");
				}

				SOAPHeader sHeader = sEnvelope.getHeader();
				OMElement sBody = createWorkItem.getOMElement(null, OMAbstractFactory.getOMFactory());
				if (sHeader != null)
					soapHeader = sHeader.toString();
				soapBody = sBody.toString();

				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						" SUSH reqSource is " + reqSource);
				if (reqSource.equals("NEWGUI")) {
					soapBody = (String) msgContext.getProperty("GUISOAPREQUEST");
					if (soapBody.indexOf("<soapenv:Header") != -1) {
						int startIdx = soapBody.indexOf("<soapenv:Header");
						soapBody = soapBody.substring(startIdx, soapBody.length());
						IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
								" SUSH soapBody Finally is is " + soapBody);
					}
					// envString = null;
					envString = "<?xml version='1.0' encoding='utf-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns0=\"http://lucent.com/ims/intf3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
							+ soapBody;
				}

				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						" SUSH envString after  is " + envString);
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						" SUSH soapBody after  is " + soapBody);

				if (envString == null || envString.equals(null)) {
					envString = "<?xml version='1.0' encoding='utf-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns0=\"http://lucent.com/ims/intf3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
							+ soapHeader + "<soapenv:Body>" + soapBody + "</soapenv:Body></soapenv:Envelope>";
				}

				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"SoapInput=" + envString);

				VSAMSoap2XMLDataConverter.ConvertToXMLHashMap(envString, soapBody, map);
			} catch (Exception ex) {
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
						"Exception extracting SoapBody:" + IdcmTracer.getStackTraceAsString(ex));
			}
		}

		try {
			workItemId = sentWir.generateWorkItemReqId(syncWorkItemPrefix);
			sentWir.setWorkItemId(workItemId);
		} catch (IdcmErrMsgException ex) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
					"Could Not generate workitemID:" + ex.toString());
			String errStr = "Unexpected internal system error.  " + ex.toString();
			setError("000", "UnexpectedError", errStr, wmWir);
			isError = true;
			wiStatus = "WI_NOT_CREATED";
			Remarks = "Could Not generate workitemID";
		}

		String reqType = null;
		if (!isError) {
			try {
				sentWir.setParameters(map);
				sentWir.putHierarchicalParamItem(Priority, "999");
				sentWir.putHierarchicalParamItem("loggedUser", userId);
				sentWir.setDataType(syncWorkItemDataType);
				sentWir.setUserId(userId); // always expected and real user from
											// GUI
				// sentWir.putParamItem(REQUESTAPPLTAG, applicationTag);

				if (soapBody.indexOf("<key>reqType</key>") != -1) {
					if (soapBody.indexOf("<value>NEWGUI</value>") != -1
							|| soapBody.indexOf("<value>GUI</value>") != -1) {
						sentWir.putHierarchicalParamItem("reqType", "GUI");
						reqType = "GUI";
					}
				} else {
					sentWir.putHierarchicalParamItem("reqType", syncWorkItemType);
					reqType = "API";
				}
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
						"Exception putting data to WIR");
			}
		}
		long convertXml = System.currentTimeMillis();

		String actionCode = null;
		String service = null;
		String applicationTag = null;
		String appTag = null;

		if (!isError) {
			Object actionCodeObj = sentWir.getParamItem("actionCode");
			if (actionCodeObj instanceof String) {
				actionCode = (String) actionCodeObj;
			}

			Object serviceObj = sentWir.getParamItem("service");
			if (serviceObj instanceof String) {
				service = (String) serviceObj;
			}

			// User application tag added for PSA feature.
			try {
				appTag = getApplicationTag(userId);
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Could not get applicationTag Value.", "  Exception: ", ex.toString());
			}
			String psaenabled = null;
			psaenabled = IdcmFramework.getConfigParameter("PSA_ENABLED");
			if (psaenabled != null && (psaenabled.equalsIgnoreCase("YES") || psaenabled.equalsIgnoreCase("TRUE"))) {

				String requestAppTag = null;
				requestAppTag = (String) sentWir.getParamItem(REQUESTAPPLTAG);
				if ((appTag == null || appTag.length() == 0)
						&& (requestAppTag == null || requestAppTag.length() == 0)) {
					IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, methodName,
							"Service Activator is null .");
					String errStr = "Service Activator is null ";
					setError("000", "UnexpectedError", errStr, wmWir);
					isError = true;
					wiStatus = "WI_NOT_CREATED";
					Remarks = "Service Activator is null";

				}
				if (appTag != null && requestAppTag != null && !appTag.equals(requestAppTag)) {
					IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, methodName,
							requestAppTag + " Service Activator is not autherized  .");
					String errStr = requestAppTag + " Service Activator is not autherized  ";
					setError("000", "UnexpectedError", errStr, wmWir);
					isError = true;
					wiStatus = "WI_NOT_CREATED";
					Remarks = "Service Activator is not autherized ";
				}
				if (appTag != null && appTag.length() > 0) {
					applicationTag = appTag;
				} else if (requestAppTag != null && requestAppTag.length() > 0) {
					applicationTag = requestAppTag;
				}
			} else {
				if (appTag != null && appTag.length() > 0) {
					applicationTag = appTag;
				} else {
					String reqAppTag = (String) sentWir.getParamItem(REQUESTAPPLTAG);
					if (reqAppTag != null && reqAppTag.length() > 0)
						applicationTag = reqAppTag;
					else
						applicationTag = "ACT";
				}
			}

			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"applicationTag is " + applicationTag);

			sentWir.putParamItem(REQUESTAPPLTAG, applicationTag);

			// Add a check to see if MAX_LICENSED_NUMBER > TOTAL_LICENSED_NO
			// Get ApplicationTag

			int total_Licensed_no = 0;
			int max_Licensed_no = 0;
			if (actionCode.startsWith("Add")) {
				if (featureLockDataMap != null && featureLockDataMap.size() > 0) {
					if (featureLockDataMap.containsKey(applicationTag)) {
						String key = (String) featureLockDataMap.get(applicationTag);
						total_Licensed_no = Integer.parseInt(key);
					}
				}

				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"total_Licensed_no is " + total_Licensed_no);

				if (wiCountMap != null && wiCountMap.size() > 0) {
					if (wiCountMap.containsKey(applicationTag)) {
						max_Licensed_no = (Integer) wiCountMap.get(applicationTag);
					}
				}
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"max_Licensed_no is " + max_Licensed_no);

				if ((max_Licensed_no > total_Licensed_no) && total_Licensed_no != 0) {
					isError = true;
					IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
							" Licensed number of subscriptions has been exceeded. You must purchase an additional license to keep using SAM.");
					String errStr = "Licensed number of subscriptions has been exceeded. You must purchase an additional license to keep using SAM. ";
					setError("004", "LicenseExpired", errStr, wmWir);
					isError = true;
					wiStatus = "WI_NOT_CREATED";
					Remarks = "LicenseExpired ";

				}
			}
		}

		if (!isError) {
			// Validate the CACHETABLEQUERYCOLUMNS fields e.g serviceCategory
			// exist in to the input

			try {
				if (service != null && actionCode != null && actionCode.startsWith("Add")) {
					validateServiceCacheData(service, sentWir);
				}
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						" Exception validating service cache: ", ex.toString());
				String errStr = "Exception validating service cache  " + ex.toString();
				setError("000", "UnexpectedError", errStr, wmWir);
				isError = true;
				wiStatus = "WI_NOT_CREATED";
				Remarks = " Exception validating service cache";
			}
			aftercache = System.currentTimeMillis();

			try {
				validateUpdateServicecacheData(sentWir);
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, methodName,
						"Could not get ServiceCacheData entries ", "  Exception: ", ex.toString());
				String errStr = "Could not get ServiceCacheData entries.  Exception:" + ex.toString();
				setError("000", "UnexpectedError", errStr, wmWir);
				isError = true;
				wiStatus = "WI_NOT_CREATED";
				Remarks = " Could not get ServiceCacheData entries ";
			}

			aftercacheupdate = System.currentTimeMillis();

			List<String> dataMapList = null;
			try {
				// Get any appropriate DataMap entries from
				// WorkItemConfiguration.
				dataMapList = GetWorkItemConfiguration.getWorkFlowMiscConfigList(DATAMAP_TYPE, sentWir);
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, methodName,
						"Could not get DataMap entries from WorkItemConfiguration.  Exception: ", ex.toString());
				String errStr = "Could not get DataMap entries from WorkItemConfiguration.. Exception:" + ex.toString();
				setError("000", "UnexpectedError", errStr, wmWir);
				isError = true;
				wiStatus = "WI_NOT_CREATED";
				Remarks = " Could not get DataMap entries from WorkItemConfiguration";
			}
			if (dataMapList == null) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"No matching rows in WorkItemConfiguration for " + DATAMAP_TYPE);
			}

			else {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						dataMapList.size() + " matching row(s) in WorkItemConfiguration for " + DATAMAP_TYPE);
				for (String dataMap : dataMapList) {
					String[] dataMapArray = dataMap.split("\\|", 2);
					if (dataMapArray != null && dataMapArray.length == 2) {
						Object oldValue = null;
						try {
							oldValue = sentWir.getHierarchicalParamItem(dataMapArray[0]);
						} catch (IdcmErrMsgException ex) {
							// Ignore it.
						}
						// Set the DataMap entry in wir.parameters if it's not
						// already set.
						if (oldValue == null) {
							try {
								sentWir.putHierarchicalParamItem(dataMapArray[0], dataMapArray[1]);
							} catch (IdcmErrMsgException ex) {
								IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, methodName,
										"Could not set DataMap entry " + dataMap + " in WorkItemRequest.  Exception: ",
										ex.toString());
								String errStr = "Could not set DataMap entry " + dataMap
										+ " in WorkItemRequest.  Exception: " + ex.toString();
								setError("000", "UnexpectedError", errStr, wmWir);
								isError = true;
								wiStatus = "WI_NOT_CREATED";
								Remarks = " Could not set DataMap entry ";
							}
						}
					}
				}
			}
		}
		long afterdatamap = System.currentTimeMillis();

		// Main_IMS_Retrieve:TS_ALT:OTE
		String WfObj = null;
		String taskModelName = null;
		if (!isError) {
			try {
				WfObj = GetWorkItemConfiguration.getWorkFlowMiscConfig("WORKFLOW", sentWir);
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Could not get workflow entries from WorkItemConfiguration.", "  Exception: ", ex.toString());
				String errStr = "Could not get workflow entries from WorkItemConfiguration.  " + ex.toString();
				setError("000", "UnexpectedError", errStr, wmWir);
				isError = true;
				wiStatus = "WI_NOT_CREATED";
				Remarks = " Could not get workflow entries from WorkItemConfiguration";
			}

			if (WfObj != null) {
				if (WfObj.indexOf(":") != -1) {
					int intI = WfObj.indexOf(":");
					taskModelName = WfObj.substring(0, intI);
				}
			}
		}

		IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
				"\nTASKMODELNAME is = " + taskModelName);

		long befWK = System.currentTimeMillis();

		balanceTimeOut = maxProcessTimeOut - (System.currentTimeMillis() - startCurrMilliSec);
		IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName, "\n"
				+ " DEBUG::maxProcessTimeOut:" + maxProcessTimeOut + "\n" + " DEBUG::balanceTimeOut:" + balanceTimeOut);

		if (balanceTimeOut <= 0) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Processing TimedOut before creating workitem");
			setError("002", "UnexpectedError", "ProcessingTimeOut", wmWir);
			isError = true;
			msgContext.setProperty(REQUESTSTATUS, "ProcessingTimeOut");
			wiStatus = "WI_NOT_CREATED";
		}

		String MetadataObjectName = null;

		if (!isError) {
			try {
				MetadataObjectName = GetWorkItemConfiguration.getWorkFlowMiscConfig("METADATA", sentWir);
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Could not get Metadata entries from WorkItemConfiguration.", "  Exception: ", ex.toString());

			}
		}

		String OperationId = (String) msgContext.getProperty("OPERATIONID");

		if ((OperationId == null) || (OperationId.equals(""))) {
			// OperationId = "ID" + System.currentTimeMillis() +
			// NBActivator.getOPIdPrefix();
			Random rand = new Random();
			int numNoRange = rand.nextInt();
			OperationId = "ID" + System.currentTimeMillis() + numNoRange;
		}
		try {
			String preErr = (String) msgContext.getProperty("REQUESTSTATUS");
			java.util.Date reqInTime = null;

			if ((hostName == null) || (hostName.equals("")))
				hostName = "localhost";

			reqInTime = new java.util.Date(startCurrMilliSec);

			NBActivityLogUtil.insertNBActLog(OperationId, userId, reqInTime, reqType, "OTE", workItemId,
					MetadataObjectName, hostName, applicationTag, "");
		} catch (IdcmErrMsgException exe) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Insert to NB_ActivityLog is failed.", "  Exception: ", exe.toString());
		}

		long aftWK = System.currentTimeMillis();
		if (!isError) {
			try {
				wkShmMap.put("COUNTDOWN", startSignal);
				instance1.setValue(workItemId, wkShmMap);
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Added Object to Shared Memory with WorkitemId=" + workItemId);
				if (!initJmsFlag) {
					synchronized (syncJMS) {
						initJMS();
						initJmsFlag = true;
					}
				}

				svcName = getSvcName();
				sentWir.setTaskModelName(taskModelName);
				String retRsp = sendToPE(sentWir, balanceTimeOut, svcName);
				NBActivator.dcrCurrentActiveThreads();
				cntDcr = true;
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Return Response is :" + retRsp);

				long createMilliSec = System.currentTimeMillis();
				msgContext.setProperty("WKCREATETIME", String.valueOf(createMilliSec));
			} catch (Exception ex) {
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
						"Exception During Create:" + ex.toString());
				String errStr = "Unexpected internal system error.  " + ex.toString();
				setError("002", "UnexpectedError", errStr, wmWir);
				isError = true;
				wiStatus = "WI_NOT_CREATED";
				Remarks = "Unexpected internal system error";
			}
		}

		// Meatadata insert

		if (!isError) {
			if (MetadataObjectName != null && MetadataObjectName
					.length() != 0) {/*
										 * try { String blobData =
										 * "<dummy></dummy>"; IdcmTracer.trace
										 * (IdcmTracer.DEFAULT_CATEGORY,
										 * IdcmTracer.DEBUG,className,
										 * methodName,
										 * "\nAbout to invoke Metadata=" +
										 * service );
										 * SearchDataUtil.populateMetadata(
										 * envString, MetadataObjectName,
										 * workItemId, userId,
										 * applicationTag,service,blobData,
										 * actionCode,false); }
										 * catch(IdcmErrMsgException ex) {
										 * IdcmTracer.trace(IdcmTracer.
										 * IDCMAPI_CATEGORY, IdcmTracer.ERROR,
										 * className, methodName,
										 * "Could not insert Metadata entries.",
										 * "  Exception: ", ex.toString());
										 * String errStr =
										 * "Unexpected internal system error.  "
										 * + ex.toString(); setError( "002",
										 * "UnexpectedError" , errStr , wmWir );
										 * isError = true; }
										 */
			}
		}

		long aftCr = System.currentTimeMillis();
		String wkStat = null;
		String workItemStatus = null;
		if (!isError) {
			if (instance1 != null) {
				boolean latchTimeOut = false;
				try {
					latchTimeOut = startSignal.await(balanceTimeOut, TimeUnit.MILLISECONDS);
				} catch (InterruptedException Ex) {
					IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
							"Latch Timeout is interrupted" + IdcmTracer.getExceptionTraceAsString(Ex));
					latchTimeOut = false;
				}

				if (latchTimeOut == false) // timeout
				{
					IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
							"latch timed out in SyncServiceskeleton");

					if (asyncResponseRequired != null && asyncResponseRequired.equals("YES")) {
						wkStat = "IN_PROGRESS";
						String errorMessage = "Experiencing delay at NE Level , response will be sent to async";
						setError("002", errorMessage, "ProcessingTimeOut", wmWir);
						isError = true;

					} else {
						msgContext.setProperty(REQUESTSTATUS, "ProcessingTimeOut");
						setError("002", "UnexpectedError", "ProcessingTimeOut", wmWir);
						isError = true;
					}
				} else {
					retSvcName(svcName);
					HashMap statMap = (HashMap) instance1.getValue(workItemId);
					IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
							"No latch time out" + statMap);

					wkStat = (String) statMap.get("STATUS");
					String extStr = (String) statMap.get("Extension");
					String new1 = (String) statMap.get("notificationType");
					IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
							"DEBUG::Extension header data " + extStr + " wkstat" + wkStat + "new1 = " + new1);
					String input = "";
					String output = "";
					com.alu.sam.nbapi.notify2.axis2gen.SOAPResponseData in0 = (com.alu.sam.nbapi.notify2.axis2gen.SOAPResponseData) statMap
							.get("NOTIFYDATA");

					IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
							"WORK ITEM STATUS" + wkStat);
					try {
						HashMap NotifyParameters = new HashMap();
						com.alu.sam.nbapi.notify2.axis2gen.Param notParam = in0.getAttributesMap();
						NotifySoapDataConverter.ConvertToHashMap((Object) notParam, (Map) NotifyParameters);
						sentWir.setParameters(NotifyParameters);
						workItemStatus = in0.getWMSoapWorkItem().getStatus();
						IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
								"WORK ITEM STATUS" + workItemStatus);
						IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
								"DEBUG::NOTIFICATION BLOB=" + sentWir);

						HashMap statusMap = new HashMap();
						statusMap.put("WorkItemStatus", workItemStatus);
						wmWir.putHierarchicalParamItem("WorkItemStatus", statusMap);

						Object obj = sentWir.getHierarchicalParamItem("Extension");
						IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
								"DEBUG:: Extension obj is" + obj + "...");

						try {
							ArrayList<HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>> outerArrayList = new ArrayList<HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>>();
							outerArrayList = (ArrayList) obj;
							IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
									"DEBUG:: Extension obj is" + outerArrayList + "...");
							ArrayList<HashMap<String, String>> subArrayList = new ArrayList<HashMap<String, String>>();
							ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> mainArrayList = new ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>();

							StringBuilder extensionString = new StringBuilder("<Extension>");

							for (int j = 0; j < outerArrayList.size(); j++) {
								for (Map.Entry<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> outerMap : outerArrayList
										.get(j).entrySet()) {
									extensionString.append("<");
									extensionString.append(outerMap.getKey());
									extensionString.append(">");
									mainArrayList = outerMap.getValue();
									IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className,
											methodName, "DEBUG::inside" + mainArrayList);
									for (int i = 0; i < mainArrayList.size(); i++) {

										for (Map.Entry<String, ArrayList<HashMap<String, String>>> mainMap : mainArrayList
												.get(i).entrySet()) {

											extensionString.append("<");
											extensionString.append(mainMap.getKey());
											extensionString.append(">");

											Object mapString = (Object) mainMap.getValue();

											if (mapString instanceof String)
												extensionString.append(mainMap.getValue());
											else {
												subArrayList = mainMap.getValue();
												IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG,
														className, methodName, "DEBUG::inside Else" + subArrayList);
												for (int k = 0; k < subArrayList.size(); k++) {

													for (Map.Entry<String, String> subMap : subArrayList.get(k)
															.entrySet()) {
														extensionString.append("<");
														extensionString.append(subMap.getKey());
														extensionString.append(">");
														if (subMap.getValue() instanceof String) {
															extensionString.append(subMap.getValue());
														}
														extensionString.append("</");
														extensionString.append(subMap.getKey());
														extensionString.append(">");
													}
												}

											}

											extensionString.append("</");
											extensionString.append(mainMap.getKey());
											extensionString.append(">");

										}
									}
									extensionString.append("</");
									extensionString.append(outerMap.getKey());
									extensionString.append(">");
								}
							}
							extensionString.append("</Extension>");
							String res = extensionString.toString();
							IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
									"DEBUG:: Extension string is" + res);
							if (obj != null) {
								HashMap guiRespMap = new HashMap();
								guiRespMap.put("Extension", res);
								IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
										"DEBUG:: Extension string is" + guiRespMap);

								if (reqSource.equals("NEWGUI")) {

									wmWir.putHierarchicalParamItem("Extension", guiRespMap);
								}

							}
						} catch (IdcmErrMsgException idcmErr) {
							IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
									"Error in Extension conversion");
						}
					} catch (IdcmErrMsgException idcmErr) {
						IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
								"Error while inserting Extension object using putHier");
					}

					if (!(wkStat.equals("NOTIFYERROR"))) {
						try {
							Object obj = sentWir.getHierarchicalParamItem("WorkFlowResponse");
							String xmlLatest = "";
							String inXML = (String) obj;
							IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
									"DEBUG:: SUSH Result xml is " + inXML);

							if (reqSource.equals("NEWGUI")) {
								// Special handling for gui response, might need
								// to run configResponse xslt...let's keep that
								// later
								IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
										"DEBUG:: OBJECT Result xml is " + obj + "..." + inXML);
								Object serviceObj = sentWir.getParamItem("service");
								if (serviceObj instanceof String) {
									service = (String) serviceObj;
								}

								String xsltName = service;
								IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
										"DEBUG:: OBJECT Result serviceis" + xsltName);
								String XSLT = "ConfigGuiResponse_" + xsltName + ".xslt";
								// String XSLT =
								// "ConfigGuiResponse_"+xsltName+"_NEWGUI"+".xslt";
								xmlLatest = CreateWorkItemUtils.transformXMLLatest(inXML, XSLT);
								IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
										"DEBUG:: XML after running xslt is" + xmlLatest);
								HashMap guiRespMap = new HashMap();
								guiRespMap.put("GUIResponse", xmlLatest);
								wmWir.putHierarchicalParamItem("GUIResponse", guiRespMap);
							}

							VSAMSoap2XMLDataConverter.XmltoWir(xmlLatest, wmWir, "WorkFlowResponse");
						} catch (IdcmErrMsgException idcmErr) {
							IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
									"Error in workflowresponse conversion");
						}
					}
					if (wkStat.equals("NOTIFYERROR") || wkStat.equals("IN_PROGRESS")) {
						IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
								"NOTIFY Error scenario=" + sentWir);
						String statusComments = "";
						isError = true;

						try {
							Object obj = sentWir.getHierarchicalParamItem("WorkFlowError");
							ArrayList<HashMap<String, String>> errorArrayList = new ArrayList<HashMap<String, String>>();
							errorArrayList = (ArrayList) obj;
							StringBuilder errorString = new StringBuilder("<WorkFlowError>");
							for (int i = 0; i < errorArrayList.size(); i++) {

								for (Map.Entry<String, String> e : errorArrayList.get(i).entrySet()) {
									errorString.append("<");
									errorString.append(e.getKey());
									errorString.append(">");

									errorString.append(e.getValue());
									errorString.append("</");
									errorString.append(e.getKey());
									errorString.append(">");

								}
							}
							errorString.append("</WorkFlowError>");
							String res = errorString.toString();
							IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
									"DEBUG:: String is" + res);
							if (obj != null) {
								HashMap guiRespMap = new HashMap();
								guiRespMap.put("GUIWorkFlowError", res);

								if (reqSource.equals("NEWGUI")) {

									wmWir.putHierarchicalParamItem("GUIWorkFlowError", guiRespMap);
								}

							}
						} catch (IdcmErrMsgException idcmErr) {
							IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
									"Error in accessing workflowError when NOTIFYERROR occurs");
						}
					}
				}
				instance1.removeValue(workItemId);
			}
		}

		IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
				"SUSH Finally the response before converting is " + wmWir.getParameters());

		try {
			if (isPreProcessErr) {
				setError("002", "UnexpectedError", preStat, wmWir);
			}
		} catch (Exception e) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
					"Exception while processing error in PutHirarchicalParamItem");
		}
		long aftNo = System.currentTimeMillis();

		if (isError) {
			if (wkStat != null && wkStat.equals("IN_PROGRESS")) {
				response.setStatus("IN_PROGRESS");
				status = "IN_PROGRESS";
			} else {
				response.setStatus("Failed");
				status = "FAILED";

			}
			response.setWorkitemId(workItemId);
			response.setAttributesMap(VSAMSoap2XMLDataConverter.VSAMMapToApiMap(wmWir.getParameters()));

		} else {
			response.setStatus("OK");
			response.setWorkitemId(workItemId);
			response.setAttributesMap(VSAMSoap2XMLDataConverter.VSAMMapToApiMap(wmWir.getParameters()));
			status = "SUCCEEDED";
		}

		long befNL = System.currentTimeMillis();
		try {
			java.util.Date createTime = null;

			if (workItemStatus == null || workItemStatus.length() == 0) {
				if (wkStat != null && wkStat.equals("NOTIFYSUCCESS")) {
					workItemStatus = "PROVISIONING_SUCCESS";
				} else if (wkStat != null && wkStat.equals("IN_PROGRESS")) {
					workItemStatus = "PROVISIONING_IN_PROGRESS";
				} else {
					if (wiStatus != null && isError)
						workItemStatus = wiStatus;
					else
						workItemStatus = "PROVISIONING_ERROR";
				}
			}

			String preErr = (String) msgContext.getProperty("REQUESTSTATUS");
			if (preErr != null) {
				Remarks = preErr;
				if (preErr.indexOf("Exceeded maximum number of SAM sessions") != -1)
					workItemStatus = "SESSIONLIMIT_ERROR";
			}

			String createTimeStr = (String) msgContext.getProperty("WKCREATETIME");
			if (createTimeStr != null) {
				long lcreateTime = Long.parseLong(createTimeStr);
				createTime = new java.util.Date(lcreateTime);
			}

			NBActivityLogUtil.updateNBActLog(status, workItemId, taskModelName, workItemStatus, createTime, Remarks);
		} catch (IdcmErrMsgException e) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
					"Exception while updating records in nb_activitylog" + IdcmTracer.getStackTraceAsString(e));
		}

		UserData.dcrSessionLimit(userId);
		if (!cntDcr) {
			NBActivator.dcrCurrentActiveThreads();
		}
		NBActivator.notifySamNotifyEvent();
		createResponse.setResponse(response);

		long retCr = System.currentTimeMillis();

		// IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG,
		// className,methodName, "WKID=" + workItemId + " Total=" +
		// (System.currentTimeMillis() - startCurrMilliSec ) + " SkelParse=" + (
		// skelParse - startCurrMilliSec) + " QueTime=" + ( outQ - skelParse ) +
		// " IDCr=" + ( aftWK - befWK) + " WKCr=" + ( aftCr - aftWK) + " NotWI="
		// + ( aftNo - aftCr) + " ActLog=" + (retCr - befNL ) );

		IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
				"WKID=" + workItemId + " Total=" + (System.currentTimeMillis() - startCurrMilliSec) + " SkelParse="
						+ (skelParse - startCurrMilliSec) + " QueTime=" + (outQ - skelParse) + " ConvertXML="
						+ (convertXml - outQ) + " ValidateCache=" + (aftercache - convertXml) + " UpdateCache="
						+ (aftercacheupdate - aftercache) + " DataMap=" + (afterdatamap - aftercacheupdate) + " WFSel="
						+ (befWK - afterdatamap) + " WKCr=" + (aftCr - aftWK) + " NotWI=" + (aftNo - aftCr) + " ActLog="
						+ (retCr - befNL));

		return createResponse;

	}

	public synchronized static void init() throws IdcmErrMsgException {
		final String methodName = ".init(String[])";
		IdcmTracer.traceEntry(className, methodName, "");

		if (IdcmFramework.getConfigParameter("NO_OF_ACTIVE_THREADS") != null) {
			syncThreads = Integer.parseInt(IdcmFramework.getConfigParameter("NO_OF_ACTIVE_THREADS"));
		}
		if (IdcmFramework.getConfigParameter("SYNC_SAM_WORKITEM_TYPE") != null) {
			syncWorkItemType = IdcmFramework.getConfigParameter("SYNC_SAM_WORKITEM_TYPE");
		}
		String shortName = null;
		int intI = hostName.indexOf('.');
		if (intI != -1)
			shortName = hostName.substring(0, intI);
		String WIPrefix = "WORKITEMID_PREFIX_" + shortName;

		if (IdcmFramework.getConfigParameter(WIPrefix) != null) {
			syncWorkItemPrefix = IdcmFramework.getConfigParameter(WIPrefix);
		}
		if (IdcmFramework.getConfigParameter("SYNC_WORKITEM_DATA_TYPE") != null) {
			syncWorkItemDataType = Integer.parseInt(IdcmFramework.getConfigParameter("SYNC_WORKITEM_DATA_TYPE"));
		}
		try {
			if (IdcmFramework.getConfigParameter("NO_OF_THREAD_PER_PE") != null) {
				threadPerProcess = Integer.parseInt(IdcmFramework.getConfigParameter("NO_OF_THREAD_PER_PE"));
			}
			if (IdcmFramework.getConfigParameter("NO_OF_PE_PROCESS") != null) {
				noOfPEProcess = Integer.parseInt(IdcmFramework.getConfigParameter("NO_OF_PE_PROCESS"));
			}
			if (IdcmFramework.getConfigParameter("PENAME_PREFIX") != null) {
				pePrefix = IdcmFramework.getConfigParameter("PENAME_PREFIX");
			}
			if (IdcmFramework.getConfigParameter("ASYNC_RESPONSE_REQUIRED") != null) {
				asyncResponseRequired = IdcmFramework.getConfigParameter("ASYNC_RESPONSE_REQUIRED");
			}
		}

		catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Failed to get NoofThread and processesfor PE" + ex.toString());
		}

		if (syncThreads > (noOfPEProcess * threadPerProcess)) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.WARN, className, methodName,
					"Number of active threads in syncnb are more than PE threads");

		}

		try {
			featureLockDataMap = getFeatureLockSMTData();
		} catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Exception extracting featureLocksmtvals:" + ex.toString());
		}

		try {
			wiCountMap = new HashMap<String, Integer>();
			wiCountMap = getMaxLicensedNo();
		} catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Exception extracting getMaxLicensedNo:" + ex.toString());
		}

		IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
				"within init...wiCountMap is " + wiCountMap + " and featureLockDataMap is " + featureLockDataMap);

		RefreshCacheTaskTimer = new Timer(true);
		RefreshCacheTaskTimer.schedule(new RefreshCacheTask(), 0, 12 * 60 * 60 * 1000);

		IdcmTracer.traceReturn(className, methodName,
				new Object[] { syncWorkItemType, syncWorkItemPrefix, syncWorkItemDataType });
	}

	public void setError(String ErrorCode, String StatusCode, String Violation, WorkItemRequest setWir) {
		final String methodName = "setError()";
		IdcmTracer.traceEntry(className, methodName,
				"ErrorCode=" + ErrorCode + " StatusCode=" + StatusCode + " Violation=" + Violation);

		ArrayList resArr = new ArrayList();
		HashMap tmpMap = new HashMap();
		tmpMap.put("ErrorCode", ErrorCode);
		tmpMap.put("StatusCode", StatusCode);
		tmpMap.put("Violation", Violation);
		resArr.add(tmpMap);
		try {
			setWir.putHierarchicalParamItem("WorkFlowError", resArr);
		} catch (IdcmErrMsgException idcmErr) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Exception while filling WorkFlowError");
		}

		IdcmTracer.traceReturn(className, methodName, "");
	}

	public static synchronized void initJMS() {
		final String methodName = "initJMS()";
		IdcmTracer.traceEntry(className, methodName, "");

		IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
				"NO Of PE process=" + noOfPEProcess + " No Of Thread Per PE=" + threadPerProcess);
		if (!initJmsFlag) {
			for (int i = 0; i < noOfPEProcess; i++) {
				for (int j = 0; j < threadPerProcess; j++) {
					String svc = pePrefix + i + j;
					try {
						IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
								"Init Service=" + svc);
						JMSSyncQueue jq = new JMSSyncQueue(svc);
						jQueues.put(svc, jq);
					} catch (Exception ex) {
						IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, methodName,
								"Exception while initing JMS:" + ex.toString());
					}
				}
			}
			initJmsFlag = true;
		}
		IdcmTracer.traceReturn(className, methodName, "");

	}

	public String sendToPE(String request, String svcName, long ms, String workItemId, String userId,
			String taskModelName) {

		final String methodName = "sendToPE";
		String retStr = null;
		try {
			JMSSyncQueue jq = (JMSSyncQueue) jQueues.get(svcName);
			retStr = jq.sendRecv(request, ms, workItemId, userId, taskModelName);
		} catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
					"Exception in sendToPE" + ex.toString());
		}
		return retStr;
	}

	public String sendToPE(WorkItemRequest sentWir, long ms, String svcName) {

		final String methodName = "sendToPE";
		String retStr = null;
		try {
			JMSSyncQueue jq = (JMSSyncQueue) jQueues.get(svcName);
			retStr = jq.sendRecv(sentWir, ms);
		} catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.ERROR, className, methodName,
					"Exception in sendToPE" + ex.toString());
		}
		return retStr;
	}

	public static void retSvcName(String svcName) {
		final String methodName = "retSvcName()";
		synchronized (syncSvc) {
			freeSvc.add(svcName);
		}
	}

	public static String getSvcName() {
		final String methodName = "getSvcName()";
		String svcName = "OteMgr";
		String retSvc = "";

		synchronized (syncSvc) {
			if (freeSvc.size() > 0) {
				retSvc = (String) freeSvc.remove(0);
			} else {
				if (peprocIdx >= noOfPEProcess) {
					peprocIdx = 0;
					pethrIdx = pethrIdx + 1;
					if (pethrIdx >= threadPerProcess) {
						pethrIdx = 0;
					}
				}
				retSvc = pePrefix + peprocIdx + pethrIdx;
				peprocIdx = peprocIdx + 1;
			}
		}
		IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName, "SVCNAME=" + retSvc);
		return retSvc;
	}

	private static Map<String, String> getServiceCacheData(String service, WorkItemRequest wir)
			throws IdcmErrMsgException {
		final String funcName = ".getServiceCacheData";
		IdcmTracer.traceEntry(className, funcName, "");

		Map<String, String> serviceCacheData = new HashMap<String, String>();

		try {
			Connection con = null;
			ConnectionPool cp = null;

			Statement stmt = null;
			ResultSet rs = null;
			try {
				cp = ConnectionPool.getConnectionPool("SyncNBModule");
				if (cp != null) {
					con = cp.getConnection();
					if (con != null) {
						String umtQuery = "select CACHETABLENAME,CACHETABLEKEYCOLUMN,CACHETABLEQUERYCOLUMNS from ServiceCacheMapUMT where SERVICE = '"
								+ service + "'";
						IdcmTracer.trace(IdcmTracer.DBACCESS_CATEGORY, IdcmTracer.DEBUG, className, funcName,
								"Select Statement is = [" + umtQuery + "]");

						stmt = con.createStatement();
						rs = stmt.executeQuery(umtQuery);

						String cacheTableName = null;
						String cacheTableKeyColumn = null;
						String cacheTableQueryColumns = null;

						if (rs.next()) {
							cacheTableName = rs.getString(1);
							cacheTableKeyColumn = rs.getString(2);
							cacheTableQueryColumns = rs.getString(3);
							IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
									"cacheTableName=" + cacheTableName + " cacheTableKeyColumn=" + cacheTableKeyColumn
											+ " cacheTableQueryColumns=" + cacheTableQueryColumns);
						}
						rs.close();
						if (cacheTableName != null && cacheTableKeyColumn != null && cacheTableQueryColumns != null) {
							String key = getKeyValue(cacheTableKeyColumn, wir);
							if (key == null) {
								throw new IdcmErrMsgException(IdcmErrMsgException.IDM_SERVER_APP_ERR,
										IdcmErrMsgException.ERC_INVALID_INPUT,
										"Value for key " + cacheTableKeyColumn + " is not set in the input.");
							}
							String[] queryColumns = cacheTableQueryColumns.split(",");
							StringBuilder cacheQuery = new StringBuilder("select ");
							cacheQuery.append(cacheTableQueryColumns);
							cacheQuery.append(" from ");
							cacheQuery.append(cacheTableName);
							cacheQuery.append(" where ");
							cacheQuery.append(cacheTableKeyColumn);
							cacheQuery.append(" = '");
							cacheQuery.append(key);
							cacheQuery.append("'");
							rs = stmt.executeQuery(cacheQuery.toString());
							if (rs.next()) {
								for (int ii = 0; ii < queryColumns.length; ++ii) {
									serviceCacheData.put(queryColumns[ii], rs.getString(ii + 1));
								}
							}
						}
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
				}
				if (cp != null && con != null) {
					cp.releaseConnection(con);
				}
			}
		} catch (SQLException ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, funcName,
					"Error in retrieving data for getServiceCacheData: ", ex.toString());
		}

		IdcmTracer.traceReturn(className, funcName, serviceCacheData);
		return serviceCacheData;
	}

	private static String getKeyValue(String cacheTableKeyColumn, WorkItemRequest wir) throws IdcmErrMsgException {
		final String funcName = ".getKeyValue";
		IdcmTracer.traceEntry(className, funcName, "cacheTableKeyColumn=" + cacheTableKeyColumn);

		String key = null;
		// First look for the key value in operationList[0].newData.
		// This should work for all requests except Modify requests.
		String newData = null;
		try {
			Object newDataObj = wir.getHierarchicalParamItem(GetWorkItemConfiguration.BODY_PARAM);
			if (newDataObj instanceof String) {
				newData = (String) newDataObj;
			}
		} catch (IdcmErrMsgException ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
					GetWorkItemConfiguration.BODY_PARAM, " is not set");
		}
		if (newData != null) {
			String cacheTableKeyColumnTag = "<" + cacheTableKeyColumn + ">";
			int startP = newData.indexOf(cacheTableKeyColumnTag);
			if (startP != -1) {
				startP += cacheTableKeyColumnTag.length();
				int endP = newData.indexOf("</" + cacheTableKeyColumn + ">", startP);
				if (endP > startP) {
					key = newData.substring(startP, endP);
				}
			}
		}
		if (key == null) {
			// If the key value is not in operationList[0].newData,
			// look for the key value in operationList[0].GCL.
			// This should work for Modify requests.
			String gcl = null;
			try {
				Object gclObj = wir.getHierarchicalParamItem(GCL_PARAM);
				if (gclObj instanceof String) {
					gcl = (String) gclObj;
				}
			} catch (IdcmErrMsgException ex) {
				IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName, GCL_PARAM,
						" is not set");
			}
			if (gcl != null) {
				String cacheTableKeyColumnTest = "[" + cacheTableKeyColumn + "=";
				int startP = gcl.indexOf(cacheTableKeyColumnTest);
				if (startP != -1) {
					startP += cacheTableKeyColumnTest.length();
					if (gcl.length() > startP) {
						String closeChar = "]";
						int firstChar = gcl.codePointAt(startP);
						if (firstChar == '"') {
							++startP;
							closeChar = "\"";
						} else if (firstChar == '\'') {
							++startP;
							closeChar = "'";
						}
						int endP = gcl.indexOf(closeChar, startP);
						if (endP > startP) {
							key = gcl.substring(startP, endP);
						}
					}
				}
			}
		}
		IdcmTracer.traceReturn(className, funcName, key);
		return key;
	}

	private void validateServiceCacheData(String service, WorkItemRequest wir) throws IdcmErrMsgException {
		final String funcName = ".validateServiceCacheData";
		IdcmTracer.traceEntry(className, funcName, "");

		String errStr = null;

		try {
			Connection con = null;
			ConnectionPool cp = null;

			Statement stmt = null;
			ResultSet rs = null;
			try {
				cp = ConnectionPool.getConnectionPool("SyncNBModule");
				if (cp != null) {
					con = cp.getConnection();
					if (con != null) {
						String umtQuery = "select CACHETABLEQUERYCOLUMNS from ServiceCacheMapUMT where SERVICE = '"
								+ service + "'";
						IdcmTracer.trace(IdcmTracer.DBACCESS_CATEGORY, IdcmTracer.DEBUG, className, funcName,
								"Select Statement is = [" + umtQuery + "]");

						stmt = con.createStatement();
						rs = stmt.executeQuery(umtQuery);

						String cacheTableQueryColumns = null;

						if (rs.next()) {
							cacheTableQueryColumns = rs.getString(1);
							IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
									" cacheTableQueryColumns=" + cacheTableQueryColumns);
						}
						rs.close();
						if (cacheTableQueryColumns != null) {
							String[] queryColumns = cacheTableQueryColumns.split(",");
							for (int ii = 0; ii < queryColumns.length; ++ii) {
								String newData = null;
								try {
									Object newDataObj = wir.getHierarchicalParamItem(queryColumns[ii]);
									if (newDataObj instanceof String) {
										newData = (String) newDataObj;
									}
								} catch (IdcmErrMsgException ex) {
									IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, funcName,
											queryColumns[ii] + " is not set");
								}
								if (newData == null || newData == "") {
									if (errStr == null) {
										errStr = queryColumns[ii];
									} else {
										errStr = errStr + ", " + queryColumns[ii];
									}
								}
							}
						}
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
				}
				if (cp != null && con != null) {
					cp.releaseConnection(con);
				}
			}
		} catch (SQLException ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, funcName,
					"SQL Error in validating validateServiceCacheData: ", ex.toString());
			errStr = "SQL Error in validating validateServiceCacheData:";
		}

		if (errStr != null) {
			throw new IdcmErrMsgException(IdcmErrMsgException.IDM_SERVER_APP_ERR, IdcmErrMsgException.ERC_INVALID_INPUT,
					errStr + " Attributes not set in the input.");
		}
		IdcmTracer.traceReturn(className, funcName, "");
	}

	public static void validateUpdateServicecacheData(WorkItemRequest wir) throws IdcmErrMsgException {
		final String funcName = ".validateUpdateServicecacheData";
		IdcmTracer.traceEntry(className, funcName, "");

		// Query the cache tables in the DB for key/value data
		// associated with the input data in wir.parameters that should
		// be added to wir.parameters and to the XML document associated
		// with the name "outofband" in wir.parameters. If this data
		// conflicts with input data (i.e., the input data already has a
		// value for a specified key), keep the input data and ignore
		// the corresponding DB data.

		try {
			String actionCode = (String) wir.getParamItem("actionCode");
			String service = (String) wir.getParamItem("service");

			if (service != null && actionCode != null && !actionCode.startsWith("Add")) {
				Map<String, String> serviceCacheData = getServiceCacheData(service, wir);
				if (serviceCacheData != null && serviceCacheData.size() > 0) {
					IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
							"getServiceCacheData returned: " + serviceCacheData.toString());
					// Remove from serviceCacheData elements that conflict with
					// the input data.
					Iterator<Map.Entry<String, String>> serviceCacheIter = serviceCacheData.entrySet().iterator();
					while (serviceCacheIter.hasNext()) {
						if (wir.getParamItem(serviceCacheIter.next().getKey()) != null) { // the
																							// key
																							// is
																							// set
																							// in
																							// the
																							// input,
																							// so
																							// ignore
																							// the
																							// value
																							// from
																							// the
																							// DB
							serviceCacheIter.remove();
						}
					}
					IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
							"After removing conflicting elements, serviceCacheData = " + serviceCacheData.toString());
					// Add any remaining serviceCacheData elements to the input
					// data.
					if (serviceCacheData.size() > 0) {
						// There are some keys from the DB that are not already
						// in the input.
						// Add them to the input data.
						String outofband = null;
						Object outofbandObj = wir.getParamItem(GetWorkItemConfiguration.OUTOFBAND);
						if (outofbandObj instanceof String) {
							outofband = (String) outofbandObj;
						}
						if (outofband != null) {
							try {
								// Parse the "outofband" XML document.
								DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
								DocumentBuilder parser = factory.newDocumentBuilder();
								Document doc = parser.parse(new InputSource(new StringReader(outofband)));

								// Loop through the data to be added to the
								// input data.
								Set<Map.Entry<String, String>> serviceCacheDataSet = serviceCacheData.entrySet();
								for (Map.Entry<String, String> serviceCacheDatum : serviceCacheDataSet) {
									// Add a key/value pair to wir.parameters.
									String key = serviceCacheDatum.getKey();
									String value = serviceCacheDatum.getValue();
									wir.putParamItem(key, value);

									// Add a <key>value</key> element to
									// "outofband".
									Element elem = doc.createElement(key);
									elem.appendChild(doc.createTextNode(value));
									doc.getDocumentElement().appendChild(elem);
								}

								// Serialize the updated "outofband" XML
								// document and replace
								// the "outofband" data in wir.parameters.
								StringWriter sw = new StringWriter();
								Transformer t = TransformerFactory.newInstance().newTransformer();
								t.setOutputProperty(OutputKeys.INDENT, "yes");
								t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
								t.transform(new DOMSource(doc), new StreamResult(sw));
								wir.putParamItem(GetWorkItemConfiguration.OUTOFBAND, sw.toString());
							} catch (Exception ex) {
								IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, funcName,
										"Exception during XML parsing of " + GetWorkItemConfiguration.OUTOFBAND + ": "
												+ ex.toString());
								throw new IdcmErrMsgException(ex.toString());
							}
						}
					}
				} else {
					IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
							"getServiceCacheData returned nothing");
				}
			}
		} catch (IdcmErrMsgException ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.ERROR, className, funcName,
					"Could not get ServiceCacheData entries ..Exception: ", ex.toString());
			throw new IdcmErrMsgException(ex.toString());
		}
		IdcmTracer.traceReturn(className, funcName, "");
	}

	private static HashMap getFeatureLockSMTData() throws IdcmErrMsgException {
		final String funcName = ".getFeatureLockSMTData";
		IdcmTracer.traceEntry(className, funcName, "");

		HashMap resultData = new HashMap();

		try {
			Connection con = null;
			ConnectionPool cp = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				cp = ConnectionPool.getConnectionPool("SyncNBModule");
				if (cp != null) {
					con = cp.getConnection();
					if (con != null) {
						String umtQuery = "select KEYNAME,KEY from featurelocksmt where FEATURENAME = 'SUBSCRIBER'";
						IdcmTracer.trace(IdcmTracer.DBACCESS_CATEGORY, IdcmTracer.DEBUG, className, funcName,
								"Select Statement is = [" + umtQuery + "]");

						stmt = con.createStatement();
						rs = stmt.executeQuery(umtQuery);

						String keyName = null;
						String key = null;
						while (rs.next()) {
							keyName = rs.getString(1);
							key = rs.getString(2);
							String decryptedStr = decryptStr(key);
							String compStr = ":" + keyName;
							String count = "";
							int dIndex = decryptedStr.indexOf(compStr);
							if (dIndex != -1 && dIndex > 0) {
								count = decryptedStr.substring(0, dIndex);
							}

							IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
									"keyName = " + keyName + " count = " + count);
							resultData.put(keyName, count);
						}
						rs.close();
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
				}
				if (cp != null && con != null) {
					cp.releaseConnection(con);
				}
			}
		} catch (SQLException ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
					"Error in retrieving data for getFeatureLockSMTData: ", ex.toString());
		}
		return resultData;
	}

	private static HashMap<String, Integer> getMaxLicensedNo() throws IdcmErrMsgException {
		final String funcName = ".getMaxLicensedNo";
		IdcmTracer.traceEntry(className, funcName, "");

		HashMap<String, Integer> subCountMap = new HashMap<String, Integer>();

		try {
			Connection con = null;
			ConnectionPool cp = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				cp = ConnectionPool.getConnectionPool("SyncNBModule");
				if (cp != null) {
					con = cp.getConnection();
					if (con != null) {
						String umtQuery = "SELECT wicount,applicationTag FROM license_usage where RUNDATE=(select max(RUNDATE) from license_usage) order by applicationTag";
						IdcmTracer.trace(IdcmTracer.DBACCESS_CATEGORY, IdcmTracer.DEBUG, className, funcName,
								"Select Statement is = [" + umtQuery + "]");

						stmt = con.createStatement();
						rs = stmt.executeQuery(umtQuery);

						String appTag = null;
						String wicount = "";
						int max_Licensed_no = 0;
						while (rs.next()) {
							wicount = rs.getString(1);
							appTag = rs.getString(2);
							max_Licensed_no = Integer.parseInt(decryptStr(wicount));
							if (subCountMap != null && subCountMap.size() > 0) {
								if (subCountMap.containsKey(appTag)) {
									int count = subCountMap.get(appTag);
									max_Licensed_no = count + max_Licensed_no;
									subCountMap.put(appTag, max_Licensed_no);

								} else
									subCountMap.put(appTag, max_Licensed_no);

							} else
								subCountMap.put(appTag, max_Licensed_no);
						}
						rs.close();
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
				}
				if (cp != null && con != null) {
					cp.releaseConnection(con);
				}
			}
		} catch (SQLException ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
					"Error in retrieving data for getMaxLicensedNo: ", ex.toString());
		}
		return subCountMap;
	}

	public static String decryptStr(String encrStr) {
		byte[] p1 = new byte[7];
		p1[0] = 'g';
		p1[1] = 'a';
		p1[2] = 'f';
		p1[3] = 't';
		p1[4] = 'e';
		p1[5] = 'a';
		p1[6] = 'h';
		byte[] cb = Codec.hexDecode(encrStr);
		byte[] b = CryptUtil.decrypt(new String(p1), cb);

		String d = null;
		if (b != null && b.length > 0) {
			d = new String(b);
		}
		return d;
	}

	private static String getApplicationTag(String userId) throws IdcmErrMsgException {
		final String funcName = ".getApplicationTag";
		IdcmTracer.traceEntry(className, funcName, "");
		String appTag = "";

		try {
			Connection con = null;
			ConnectionPool cp = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				cp = ConnectionPool.getConnectionPool("");
				if (cp != null) {
					con = cp.getConnection();
					if (con != null) {
						String appTagQuery = "select APPLICATIONTAG from WM_USERPROFILEUMT where USERID = ? ";
						IdcmTracer.trace(IdcmTracer.DBACCESS_CATEGORY, IdcmTracer.DEBUG, className, funcName,
								"Select Statement is = [" + appTagQuery + "]");

						pstmt = con.prepareStatement(appTagQuery);
						pstmt.setString(1, userId);
						rs = pstmt.executeQuery();

						while (rs.next()) {
							appTag = rs.getString(1);

						}
						rs.close();
					}
				}
			} finally {
				if (pstmt != null) {
					pstmt.close();
				}
				if (cp != null && con != null) {
					cp.releaseConnection(con);
				}
			}
		} catch (SQLException ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
					"Error in retrieving data for WM_USERPROFILEUMT: ", ex.toString());
		} catch (Exception ex) {
			IdcmTracer.trace(IdcmTracer.IDCMAPI_CATEGORY, IdcmTracer.DEBUG, className, funcName,
					"Error in retrieving data for WM_USERPROFILEUMT: ", ex.toString());
		}
		IdcmTracer.traceReturn(className, funcName, appTag);
		return appTag;
	}

	public static class RefreshCacheTask extends TimerTask {
		final String className = RefreshCacheTask.class.getName();

		public RefreshCacheTask() {

		}

		public void run() {
			final String methodName = "run()";

			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Entering RefreshCacheTask.run() method.");

			try {
				featureLockDataMap = getFeatureLockSMTData();
			} catch (Exception ex) {
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Exception extracting featureLocksmtvals:" + ex.toString());
			}

			try {
				wiCountMap = new HashMap<String, Integer>();
				wiCountMap = getMaxLicensedNo();
			} catch (Exception ex) {
				IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
						"Exception extracting getMaxLicensedNo:" + ex.toString());
			}

			IdcmTracer.trace(IdcmTracer.DEFAULT_CATEGORY, IdcmTracer.DEBUG, className, methodName,
					"Within run...wiCountMap is " + wiCountMap + " and featureLockDataMap is " + featureLockDataMap);
		}
	} // End of RefreshCacheTask

}
