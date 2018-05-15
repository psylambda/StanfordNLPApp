package team.intelligenthealthcare.keywordsextraction;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


import com.google.gson.Gson;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class NerServer {
    private HttpServer server;
    private Tagger tagger;
    private Gson gson;

    public NerServer() {
        tagger = new Tagger();
        gson = new Gson();
    }

    public static void main(String[] args) throws IOException {
        NerServer server = new NerServer();
        server.start();
        server.run();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(56786), 0);
        server.createContext("/ner", new TagByNER());
        server.createContext("/stringmatch", new TagByStringMatch());
        server.setExecutor(null);
        server.start();
        //下个版本加上下面这句话，预先加载模型到内存中，防止第一次调用慢
        tagger.tagByNER("预先加载模型到内存中，防止第一次调用慢");
        tagger.tagByStringMatching("预先加载模型到内存中，防止第一次调用慢");
        //System.out.println(tmp);
    }

    public void run() {
        while (true) {
            System.out.print(">>> ");
            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.nextLine();
            System.out.println(cmd);
            if ("exit".equals(cmd)) {
                server.stop(0);
                break;
            }
        }
    }

    private class Sentence {
        public String[] words;
        public int[] tags;
    }

    private class TaggedText {
        public Sentence[] sentences;
    }

    private class UntaggedText {
        public String text;
    }

    private class TagByNER implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            String msg;

            if ("POST".equals(t.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()));
                StringBuilder builder = new StringBuilder();
                String line;
                while (null != (line = reader.readLine())) {
                    builder.append(line).append('\n');
                }
                System.out.println("/ner: " + builder.toString());
                UntaggedText input = gson.fromJson(builder.toString(), UntaggedText.class);
                TaggedText output = new TaggedText();
                String[][][] result = tagger.tagByNER(input.text);
                output.sentences = new Sentence[result.length];
                for (int i = 0; i < result.length; i++) {
                    output.sentences[i] = new Sentence();
                    output.sentences[i].words = result[i][0];
                    output.sentences[i].tags = new int[result[i][1].length];
                    for (int j = 0; j < output.sentences[i].tags.length; j++)
                        if (result[i][1][j].equals("MED"))
                            output.sentences[i].tags[j] = 1;
                        else
                            output.sentences[i].tags[j] = 0;
                }
                msg = gson.toJson(output);
                t.sendResponseHeaders(200, msg.getBytes().length);
            } else {
                msg = "Request method 'POST' required.";
                t.sendResponseHeaders(400, msg.getBytes().length);
            }
            OutputStream out = t.getResponseBody();
            out.write(msg.getBytes());
            out.close();
        }
    }

    private class TagByStringMatch implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            String msg = "";

            if ("POST".equals(t.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()));
                StringBuilder builder = new StringBuilder();
                String line;
                while (null != (line = reader.readLine())) {
                    builder.append(line).append('\n');
                }
                System.out.println("/stringmatch: " + builder.toString());
                UntaggedText input = gson.fromJson(builder.toString(), UntaggedText.class);
                TaggedText output = new TaggedText();
                String[][][] result = tagger.tagByStringMatching(input.text);
                output.sentences = new Sentence[result.length];
                for (int i = 0; i < result.length; i++) {
                    output.sentences[i] = new Sentence();
                    output.sentences[i].words = result[i][0];
                    output.sentences[i].tags = new int[result[i][1].length];
                    for (int j = 0; j < output.sentences[i].tags.length; j++)
                        if (result[i][1][j].equals("MED"))
                            output.sentences[i].tags[j] = 1;
                        else
                            output.sentences[i].tags[j] = 0;
                }
                msg = gson.toJson(output);
                t.sendResponseHeaders(200, msg.getBytes().length);
            } else {
                msg = "Request method 'POST' required.";
                t.sendResponseHeaders(400, msg.getBytes().length);
            }
            OutputStream out = t.getResponseBody();
            out.write(msg.getBytes());
            out.close();
        }
    }
}
