package io.scorecard4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * end-2-end test for scorecard4j.
 */
public class Scorecard4jTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Scorecard4jTest( String testName ){
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( Scorecard4jTest.class );
    }

    /**
     * dummy
     */
    public void testScorecard4j(){
        assertTrue( true );
    }
}
