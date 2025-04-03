package com.mybatisflex.coretest;

import com.mybatisflex.core.util.UpdateEntity;
import org.junit.Assert;
import org.junit.Test;

public class UpdateEntityTest {

    @Test
    public void testIssuesIBYEZ7() {
        Article article = UpdateEntity.of(Article.class, 1);
        Assert.assertEquals(1, (long) article.getId());
    }
}
