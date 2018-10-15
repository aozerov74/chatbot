package com.chatbot.engine;

import opennlp.tools.chatbot.ChatIterationResult;
import opennlp.tools.chatbot.LongQueryWebSearcher;
import opennlp.tools.chatbot.search_results_blender.AnaphoraProcessor;
import opennlp.tools.chatbot.search_results_blender.BlenderClarificationExpressionGenerator;
import opennlp.tools.chatbot.search_results_blender.WebDomainManager;
import opennlp.tools.chatbot.wrapper.BotResponse;
import opennlp.tools.chatbot.wrapper.SearchSessionManagerWrapper;
import opennlp.tools.parse_thicket.Pair;
import opennlp.tools.parse_thicket.kernel_interface.TreeKernelRunner;

import java.util.List;

public class QueryServer extends SearchSessionManagerWrapper {
	private LongQueryWebSearcher  searcher = LongQueryWebSearcher.getInstance();
	private BlenderClarificationExpressionGenerator clarificationExpressionGenerator = new BlenderClarificationExpressionGenerator();

	private TreeKernelRunner browser = new TreeKernelRunner();
	private WebDomainManager yelpManager = new WebDomainManager();
	private static final String WEB_DOMAIN_REGEXP = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
	private String currDomain = null;
    private String url;

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    private BotResponse run(String query) {
        BotResponse resp = new BotResponse();
        try {
            if (query.toLowerCase().equals("q"))
                return null;

            if (query.toLowerCase().startsWith("change topic")) {
                queryType = 0;
                clarificationExpressionGenerator.reset();
                resp.responseMessage = "You can ask a NEW question now";
                return resp;
            }

            currDomain = QueryUtils.isWebDomainInQuery(query);
            if (query.toLowerCase().startsWith("do order") || query.toLowerCase().startsWith("do reservation") || currDomain != null) {
                queryType = 7;
                clarificationExpressionGenerator.reset();
                resp.responseMessage = "You can request your order now";
                return resp;
            }

            if (queryType == 0) {
                return runBasicSearch(query, resp);
            }

            if (queryType == 1) {
                String selectedAnswer = clarificationExpressionGenerator.matchUserResponseWithGeneratedOptions(query);
                if (selectedAnswer != null) {
                    clarificationExpressionGenerator.latestAnswer = selectedAnswer;
                    resp.responseMessage = selectedAnswer + "\r\n" + "Are you OK with this answer? yes/more/no/specify [different topic]/ reduce search to web domain";
                    //showPage(clarificationExpressionGenerator.matchUserResponseWithGeneratedOptionsPair(query).getSecond());
                    this.url = clarificationExpressionGenerator.matchUserResponseWithGeneratedOptionsPair(query).getSecond();
                    queryType = 3;
                } else {
                    logSilent(clarificationExpressionGenerator.getBestAvailableCurrentAnswer());
                    resp.responseMessage = clarificationExpressionGenerator.getBestAvailableCurrentAnswer();
                }
                resp.setResponseObject(clarificationExpressionGenerator);
                return resp;
            } else if (queryType == 3 && query.toLowerCase().indexOf("yes") > -1) {
                queryType = 0;
                resp.responseMessage = "Now you can ask a NEW question";
                return resp;
            } else if (queryType == 3 && query.toLowerCase().indexOf("more") > -1) {
                logSilent(clarificationExpressionGenerator.getBestAvailableCurrentAnswer());
                queryType = 0;
                resp.responseMessage = clarificationExpressionGenerator.getBestAvailableCurrentAnswer() + "\nNow you can ask a NEW question";
                return resp;
            } else if (queryType == 3 && query.toLowerCase().indexOf("reduce ") > -1) {
                searcher.setQueryType(queryType);
                queryType = 0;
                String domain = extractDomainFromQuery(query);
                clarificationExpressionGenerator.setDomain(domain);
                List<ChatIterationResult> searchRes = searcher.searchLongQuery(clarificationExpressionGenerator.originalQuestion + " site:" + domain);
                logSilent(getAnswerNum(0, searchRes));
                queryType = 0;
                resp.responseMessage = "We are now trying to use the constraint on the domain " + domain + "\r\n" + getAnswerNum(0, searchRes) + "\r\nNow you can ask a NEW question";
                return resp;
            } else if (queryType == 3 && query.toLowerCase().indexOf("no") > -1) {
                queryType = 0;
                List<ChatIterationResult> searchRes = searcher.searchLongQuery(clarificationExpressionGenerator.originalQuestion + " " + clarificationExpressionGenerator.clarificationQuery);
                resp.responseMessage = "We are now trying to use the constrainst from your previous replies..." + "\r\nI think you will find this information useful:\r\n";

                for (int i = 0; i < searchRes.size(); i++) {
                    if (!clarificationExpressionGenerator.isEqualToAlreadyGivenAnswer(getAnswerNum(i, searchRes))) {
                        resp.responseMessage += getAnswerNum(i, searchRes);
                        break;
                    }
                }
                queryType = 0;
                resp.responseMessage += "\nNow you can ask a NEW question";
                return resp;
            } else if (queryType == 3 && AnaphoraProcessor.isAnaphoraQuery(query)) {
                queryType = 0; // proceed as regular initial search
                String previousQuery = clarificationExpressionGenerator.originalQuestion;
                String anaphoraQuery = AnaphoraProcessor.substituteAnaphoraExtractPhrase(previousQuery, query, searcher.getPhraseExtractor());
                //substituteAnaphora(previousQuery, query);
                return runBasicSearch(anaphoraQuery, resp);
            } else if (queryType == 3) { // default processing of user response after clarification
                String selectedAnswer = clarificationExpressionGenerator.matchUserResponseWithGeneratedOptions(query);
                if (selectedAnswer != null) {
                    clarificationExpressionGenerator.latestAnswer = selectedAnswer;
                    queryType = 3;
                    resp.responseMessage = selectedAnswer + "\r\nAre you OK with this answer? yes/more/no/specify [different topic]/reduce search result to domain";
                } else {
                    resp.responseMessage = clarificationExpressionGenerator.getBestAvailableCurrentAnswer();
                }
                return resp;
            }
            if (queryType == 4) {
                if (query.toLowerCase().equals("no")) {
                    queryType = 0;
                    resp.responseMessage = "\nNow you can ask a NEW question";
                } else {
                    String productPageSeaarchResult = clarificationExpressionGenerator.searchProductPage(query);
                    resp.responseMessage = "Let me tell you more about " + clarificationExpressionGenerator.currentEntity + "\r\n";
                    resp.responseMessage += productPageSeaarchResult + "\n More questions about this product?\n";
                }
                return resp;
            }

            if (queryType == 7) {
                yelpManager.setWebDomain(currDomain);
                String url = yelpManager.formYelpRequestURL(query);
                if (url != null) {
                    this.url = url;
                    resp.responseMessage = "You can make your choice and order";
                } else {
                    resp.responseMessage = "Could not interpret your order";
                }
                queryType = 0;
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return resp;
    }

	protected BotResponse runBasicSearch(String query, BotResponse resp){
		List<ChatIterationResult> searchRes = searcher.searchLongQuery(query);
		 Pair<String, List<String>> clarificationPair = clarificationExpressionGenerator.generateClarificationPair(query, searchRes);
		// no clarification needed, so just give response as a first paragraph text
		String clarificationStr = clarificationPair.getFirst();
		if (clarificationStr == null){
			resp.responseMessage = "I think you will find this information useful : " + getAnswerNum(0, searchRes);
			//showPage(getAnswerUrl(0, searchRes));
            this.url = getAnswerUrl(0, searchRes);
			queryType = 0;
		} else {
			resp.responseMessage = "I believe these are the main topics of your query: is that what you meant? Please select \r\n"+ clarificationStr;
			resp.setClarificationOptions(clarificationPair.getSecond());
			queryType = 1;
		}
		return resp;
	}

	public String getUrl() {
	    return url;
    }

	public static final ChatbotResponse getResponse(int queryType, String query) {
        QueryServer server = QueryServerContext.getInstance().getQueryServer();
        server.setQueryType(queryType);
        BotResponse resp = server.run(query);
        ChatbotResponse response = new ChatbotResponse();
        response.setBotResponse(resp);
        response.setUrl(server.getUrl());
        response.setQueryType(server.queryType);
        return response;
    }
}

