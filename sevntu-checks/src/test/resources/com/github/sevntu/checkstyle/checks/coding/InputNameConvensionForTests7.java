package com.revere.tms.task;

import java.util.HashSet;

import java.util.Set;

import org.junit.Assert;

import org.junit.Before;

import org.junit.Test;

import com.revere.tms.common.Reference;

public class InputNameConvensionForTestsReferencesUpdate 
{

    private static final Set<Reference> EMPTY_REF_SET = new HashSet<Reference>();

    private Set<Reference> existingReferences;

    private Set<Reference> updatedReferences;

    @Before
    public void setUp()
            throws Exception
    {

        existingReferences = new HashSet<Reference>();

        existingReferences.add(Reference.newDocumentRef(
                "http://www.wiki.reveredata.com", "WikiPage", null));

        updatedReferences = new HashSet<Reference>(2);

        updatedReferences.add(Reference.newDocumentRef(
                "http://www.reveredata.com", "HomePage", null));

        updatedReferences.add(Reference.newDocumentRef(
                "http://www.wiki.reveredata.com", "WikiPage", null));

    }

    @Test
    public void testApplyToOverwriteIsTrue()
    {

        // Check apply empty references (overwrite = TRUE)

        {

            ReferencesUpdate refUpdate = new ReferencesUpdate(EMPTY_REF_SET,
                    true);

            Set<Reference> result1 = refUpdate.applyTo(EMPTY_REF_SET);

            Assert.assertEquals(0, result1.size());

            Set<Reference> result2 = refUpdate.applyTo(existingReferences);

            Assert.assertEquals(0, result2.size());

        }

        // Check apply references (overwrite = TRUE)

        {

            ReferencesUpdate refUpdate = new ReferencesUpdate(
                    updatedReferences, true);

            Set<Reference> result1 = refUpdate.applyTo(EMPTY_REF_SET);

            Assert.assertEquals(2, result1.size());

            Assert.assertNotSame(updatedReferences, result1);

            Set<Reference> result2 = refUpdate.applyTo(existingReferences);

            Assert.assertEquals(2, result2.size());

            Assert.assertNotSame(updatedReferences, result2);

        }

    }

    @Test
    public void testApplyToOverwriteIsFalse()
    {

        // Check apply empty references (overwrite = FALSE)

        {

            ReferencesUpdate refUpdate = new ReferencesUpdate(EMPTY_REF_SET,
                    false);

            Set<Reference> result1 = refUpdate.applyTo(EMPTY_REF_SET);

            Assert.assertEquals(0, result1.size());

            Set<Reference> result2 = refUpdate.applyTo(existingReferences);

            Assert.assertEquals(1, result2.size());

            Assert.assertNotSame(existingReferences, result2);

        }

        // Check apply references (overwrite = FALSE)

        {

            ReferencesUpdate refUpdate = new ReferencesUpdate(
                    updatedReferences, false);

            Set<Reference> result1 = refUpdate.applyTo(EMPTY_REF_SET);

            Assert.assertEquals(2, result1.size());

            Assert.assertNotSame(updatedReferences, result1);

            Set<Reference> result2 = refUpdate.applyTo(existingReferences);

            Assert.assertEquals(2, result2.size());

            Assert.assertNotSame(updatedReferences, result2);

        }

    }

    @Test
    public void testApplyToAndOverwriteSameReferences()
    {

        Reference ref1 = Reference.newDocumentRef("http://www.reveredata.com",
                "HomePage", null);

        Reference ref2 = Reference.newDocumentRef("http://www.reveredata.com",
                "HomePage", null);

        Set<Reference> refs = new HashSet<Reference>();

        refs.add(ref1);

        Set<Reference> existingRefs = new HashSet<Reference>();

        existingRefs.add(ref2);

        ReferencesUpdate refUpdate = new ReferencesUpdate(refs, false);

        Set<Reference> result = refUpdate.applyTo(existingRefs);

        Assert.assertEquals(1, result.size());

        Assert.assertSame(ref2, result.iterator().next());

    }

}
