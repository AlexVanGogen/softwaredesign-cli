package ru.spbau.mit.parse;

import org.junit.After;
import org.junit.Before;

import java.util.List;

import static org.junit.Assert.*;

public class ParserTest {

    Parser parser;

    @Before
    public void setUp() throws Exception {
         parser = new Parser();
    }

    @After
    public void tearDown() throws Exception {
        parser = null;
    }

    @org.junit.Test
    public void parseEmptyString() {
        // given
        String input = "";
        // when
        List<String> tokens = parser.parse(input);
        // then
        assertEquals(tokens.size(), 0);
    }

    @org.junit.Test
    public void parseAssignment() {
        // given
        String input = "foo=bar test";
        // when
        List<String> tokens = parser.parse(input);
        // then
        assertEquals(tokens.size(), 2);
        assertEquals(tokens.get(0), "foo=bar");
    }


    @org.junit.Test
    public void parseAssignmentWhitespace() {
        // given
        String input = "foo = bar";
        // when
        List<String> tokens = parser.parse(input);
        // then
        assertEquals(tokens.size(), 3);
    }

    @org.junit.Test
    public void parsePipes() {
        // given
        String input = "foo=bar | test | another";
        // when
        List<String> tokens = parser.parse(input);
        // then
        assertEquals(tokens.size(), 5);
        assertEquals(tokens.get(4), "another");
    }

    @org.junit.Test
    public void parsePipesWhitespace() {
        // given
        String input = "echo $foo |       test |         another";
        // when
        List<String> tokens = parser.parse(input);
        // then
        assertEquals(tokens.size(), 6);
        assertEquals(tokens.get(5), "another");
    }

    @org.junit.Test
    public void parseQuotes() {
        // given
        String input = "echo \"$foo\" '|       test |'         another";
        // when
        List<String> tokens = parser.parse(input);
        // then
        assertEquals(tokens.size(), 4);
        assertEquals(tokens.get(1), "$foo");
    }
}