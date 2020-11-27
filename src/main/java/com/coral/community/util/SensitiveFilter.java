package com.coral.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/*
*  1. define pre-fix trie
*  2. Initialize pre-fix trie according to the Banned Word
*  3. Define method to filter Banned Word (3 pointer)
* */

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter .class);

    // replaced with char
    private static final String REPLACEMENT = "***";

    // root
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
          try(
                  // read banned words file from classes
                  // after compile, the txt file will be in /target/classes
                  InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                  BufferedReader reader = new BufferedReader(new InputStreamReader(is));
          ){
              String keyword;
              while((keyword = reader.readLine()) != null ){
                  // add to trie
                  this.addKeyword(keyword);
              }

         }catch (IOException e){
              logger.error("load sensitive file failure : " +  e.getMessage());
          }
    }
    // put an word to the trie
    private void addKeyword(String keyword){
        // abc => root -> a -> b  -> c
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                // initilize
                subNode=new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // pointer point to the subNode
            tempNode = subNode;

            // set End identification
            if( i == keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * filter baned words
     *
     * @param text text need to go through the filter
     * @return text after filter
     */

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        // pointer 1 : point to the root first
        TrieNode tempNode = rootNode;

        // pointer 2(start)/3(end)
        int begin = 0;
        int position = 0;

        // result
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);
            // skip special Symbol, symbol(123abc)
            if(isSymbol(c)){
                // pointer on the root, special symbol store in result, p2 move to next: *f*uc
                if(tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                // p3 have to move one step
                position++;
                continue;
            }

            // check the next node
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null) {
                // current: p2---p3: no baned word
                sb.append(text.charAt(begin));
                // next position
                position = ++begin;
                // p1 point to rootNode
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                sb.append(REPLACEMENT);

                begin = ++position;
                tempNode=rootNode;
            }else{
                // check the next char
                position++;
            }
        }
        // put the last chars into sb
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // check current char is Symbol
    private boolean isSymbol(Character c){
        // c<0x2E80 || c>0x9FFF : chinese/japanese/korean
        // return CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);
        //isAsciiAlphanumeric(c): abc123->true %^&*$#@->false
        return !CharUtils.isAsciiAlphanumeric(c);

    }

    // pre-fix trie
    private class TrieNode{

        // banned word finish indication
        private boolean isKeyWordEnd = false;
        // child node (key:next Char, value:next node)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        // add subNodes
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }
        // get subNode
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
