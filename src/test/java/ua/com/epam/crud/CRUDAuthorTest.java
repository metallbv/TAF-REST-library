package ua.com.epam.crud;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import ua.com.epam.BaseTest;
import ua.com.epam.entity.author.Author;

import static ua.com.epam.config.URI.GET_AUTHOR_SINGLE_OBJ;
import static ua.com.epam.config.URI.POST_AUTHOR_SINGLE_OBJ;

// yes, that looks pretty simple... but how to resolve
// problem if it will be needed to make get call
// with query parameters..? (like ?key1=value1&key2=value2)
// and then check if our response is sorted by
// some parameter... or is paginated or something else?
// try to solve it on service layer

// here are two HTTP operations (POST and GET)
// and pretty simple tests that check if them work correctly
@Test(description = "CRUD operations for author table")
public class CRUDAuthorTest extends BaseTest {
    private Author expA = testData.authors().getRandomOne();

    @Test(description = "Create author test")
    public void postAuthor() {
        client.post(POST_AUTHOR_SINGLE_OBJ, expA);             // post author
        String body = client.getResponse().getBody();          // get body from response as String

        int statusCode = client.getResponse().getStatusCode(); // get response status code
        Author actA = g.fromJson(body, Author.class);          // map response String to Author obj

        Assert.assertEquals(statusCode, 201);           // verify that statusCode is 201
        Assert.assertEquals(actA, expA);                       // verify that Author object is equal to expected
    }

    //make it depends from previous test because we use
    //post without checking it functionality so, if post
    //not working, it will be not possible to check get
    //TestNG will skip it
    @Test(description = "Get author test", dependsOnMethods = "postAuthor")
    public void getAuthor() {
        //precondition, author must exist in API data base
        client.post(POST_AUTHOR_SINGLE_OBJ, expA);

        // try to take out this on service layer
        client.get(String.format(GET_AUTHOR_SINGLE_OBJ, expA.getAuthorId()));
        String body = client.getResponse().getBody();

        int statusCode = client.getResponse().getStatusCode();
        Author actA = g.fromJson(body, Author.class);

        // try to take out this on service layer in some validator class
        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(actA, expA);
    }

    //and clear all posted data
    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        clean.authors();
    }
}
