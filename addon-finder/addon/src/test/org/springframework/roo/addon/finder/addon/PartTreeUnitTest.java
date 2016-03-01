package org.springframework.roo.addon.finder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.roo.addon.finder.parser.PartTree;
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.details.DefaultClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.DefaultFieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.MemberHoldingTypeDetails;
import org.springframework.roo.classpath.scanner.MemberDetailsImpl;
import org.springframework.roo.model.CustomDataImpl;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;

/**
 * Unit tests for {@link PartTree}.
 * 
 * @author Paula Navarro
 * @since 2.0
 */
public class PartTreeUnitTest {


  private String[] PREFIXES = {"find", "read", "query"};
  private String[] DISTINCT = {"", "Distinct"};
  private String[] LIMIT = {"Top", "First"};
  private String[] TEST_LIMIT = {"Top", "First", "", "Top10", "First10"};
  private String[] NUMBERS = {"", "1", "5", "10", "100"};

  private String[] PROPERTIES = {"Text", "Number", "Date", "Enumer", "PrimitiveInt"};

  private String[] CONJUCTIONS = {"Or", "And"};
  private String[] IGNORE_CASE = {"IgnoreCase", "IgnoringCase"};
  private String[] ALL_IGNORE_CASE = {"AllIgnoreCase", "AllIgnoringCase"};

  private String[] STRING_OP = {"Containing", "Contains", "EndingWith", "EndsWith", "Equals", "Is",
      "Like", "Matches", "MatchesRegex", "Not", "In", "NotIn", "NotContaining", "NotContains",
      "NotLike", "NotNull", "Null", "Regex", "StartingWith", "StartsWith"};

  private String[] STRING_IS_OP = {"", "Containing", "EndingWith", "Like", "Not", "NotContaining",
      "NotLike", "NotNull", "Null", "StartingWith", "In", "NotIn"};


  private String[] NUMBER_OP = {"Equals", "Is", "Not", "NotNull", "Null", "GreaterThan",
      "GreaterThanEqual", "In", "NotIn", "LessThan", "Between", "LessThanEqual"};

  private String[] NUMBER_IS_OP = {"", "Not", "NotNull", "Null", "GreaterThan", "GreaterThanEqual",
      "In", "NotIn", "LessThan", "Between", "LessThanEqual"};

  private String[] PRIMITE_NUMBER_OP = {"Equals", "Is", "Not", "GreaterThan", "GreaterThanEqual",
      "In", "NotIn", "LessThan", "LessThanEqual", "Between"};

  private String[] PRIMITE_NUMBER_IS_OP = {"", "Equals", "Not", "GreaterThan", "GreaterThanEqual",
      "In", "NotIn", "LessThan", "LessThanEqual", "Between"};

  private String[] DATE_OP = {"Equals", "In", "NotIn", "Is", "Not", "NotNull", "Null", "Before",
      "After", "Between"};

  private String[] DATE_IS_OP = {"", "Not", "In", "NotIn", "Null", "NotNull", "Before", "After",
      "Between"};


  private MemberDetailsImpl memberDetails;



  @Before
  public void setUp() throws IllegalArgumentException, IllegalAccessException {


    List<FieldMetadata> declaredFields = new ArrayList<FieldMetadata>();

    declaredFields.add(new DefaultFieldMetadata(new CustomDataImpl(new HashMap<Object, Object>()),
        "text", 0, null, new JavaSymbolName("text"), new JavaType(String.class), null));

    declaredFields.add(new DefaultFieldMetadata(new CustomDataImpl(new HashMap<Object, Object>()),
        "number", 0, null, new JavaSymbolName("number"), new JavaType(Integer.class), null));

    declaredFields.add(new DefaultFieldMetadata(new CustomDataImpl(new HashMap<Object, Object>()),
        "date", 0, null, new JavaSymbolName("date"), new JavaType(Date.class), null));

    declaredFields.add(new DefaultFieldMetadata(new CustomDataImpl(new HashMap<Object, Object>()),
        "enumer", 0, null, new JavaSymbolName("enumer"), new JavaType(Enum.class), null));

    declaredFields.add(new DefaultFieldMetadata(new CustomDataImpl(new HashMap<Object, Object>()),
        "primitiveInt", 0, null, new JavaSymbolName("primitiveInt"), JavaType.INT_PRIMITIVE, null));


    final List<MemberHoldingTypeDetails> memberHoldingTypeDetails =
        new ArrayList<MemberHoldingTypeDetails>();

    memberHoldingTypeDetails.add(new DefaultClassOrInterfaceTypeDetails(new CustomDataImpl(
        new HashMap<Object, Object>()), "Example", 0, null, new JavaType("Example"),
        PhysicalTypeCategory.CLASS, null, declaredFields, null, null, null, null, null, null, null,
        null));

    memberDetails = new MemberDetailsImpl(memberHoldingTypeDetails);


  }

  private void test(String prefix, String[] result) {
    List<String> options = new PartTree(prefix, memberDetails).getOptions();
    assertEqualsList(generateOptions(prefix, result), options);
  }


  @Test(expected = NullPointerException.class)
  public void rejectsNullSource() throws Exception {
    new PartTree(null, memberDetails);
  }


  @Test(expected = NullPointerException.class)
  public void rejectsNullMemberDetails() throws Exception {
    new PartTree("test", null);
  }

  @Test
  public void returnsQuerys() throws Exception {
    test("", PREFIXES);
  }

  @Test
  public void badQueryOperation() throws Exception {
    assertEquals(new PartTree("find", memberDetails).isValid(), false);
    assertEquals(new PartTree("findBy", memberDetails).isValid(), false);
    assertEquals(new PartTree("findText", memberDetails).isValid(), false);
    assertEquals(new PartTree("Text", memberDetails).isValid(), false);
    assertEquals(new PartTree("delete", memberDetails).isValid(), false);
    assertEquals(new PartTree("TextBy", memberDetails).isValid(), false);
    assertEquals(new PartTree("deleteBy", memberDetails).isValid(), false);
  }

  @Test
  public void badDistinct() throws Exception {
    assertEquals(new PartTree("Distinctfind", memberDetails).isValid(), false);
    assertEquals(new PartTree("findByDistinct", memberDetails).isValid(), false);
    assertEquals(new PartTree("findTextDistinctBy", memberDetails).isValid(), false);
    assertEquals(new PartTree("findTextDistinct", memberDetails).isValid(), false);
    assertEquals(new PartTree("DistinctBy", memberDetails).isValid(), false);
    assertEquals(new PartTree("findByDistinct", memberDetails).isValid(), false);
  }

  @Test
  public void badLimit() throws Exception {
    assertFalse(new PartTree("Topfind", memberDetails).isValid());
    assertFalse(new PartTree("findTopDistinctBy", memberDetails).isValid());
    assertFalse(new PartTree("findTopFirstBy", memberDetails).isValid());
    assertFalse(new PartTree("findTopTextTextBy", memberDetails).isValid());
    assertFalse(new PartTree("findTopaTextBy", memberDetails).isValid());
    assertFalse(new PartTree("findTopaBy", memberDetails).isValid());
  }


  @Test
  public void badSubjectField() throws Exception {
    assertFalse(new PartTree("findTBy", memberDetails).isValid());
    assertFalse(new PartTree("findTeBy", memberDetails).isValid());

  }

  @Test
  public void rejectsMultipleIgnoreCase() throws Exception {
    assertFalse(new PartTree("findByTextIgnoreCaseIgnoreCase", memberDetails).isValid());
  }

  @Test
  public void rejectsMultipleAllIgnoreCase() throws Exception {
    assertFalse(new PartTree("findByTextIgnoreCaseAllIgnoreCaseAllIgnoreCase", memberDetails)
        .isValid());
  }


  @Test
  public void rejectsMultipleOrderBy() throws Exception {
    assertFalse(new PartTree("findTextOrderByTextOrderByNumber", memberDetails).isValid());
  }

  @Test(expected = RuntimeException.class)
  public void rejectsEmptyOr() throws Exception {
    assertFalse(new PartTree("findByOr", memberDetails).isValid());
  }

  @Test(expected = RuntimeException.class)
  public void rejectsEmptyAnd() throws Exception {
    assertFalse(new PartTree("findByAnd", memberDetails).isValid());
  }

  @Test(expected = RuntimeException.class)
  public void rejectsPredicateMultipleOrderBy() throws Exception {
    new PartTree("findByTextOrderByTextOrderByNumber", memberDetails);
  }

  @Test
  public void parsesIgnoreCaseAndAllIgnoreCaseCorrectly() throws Exception {
    assertTrue(new PartTree("findByTextIgnoreCaseAllIgnoreCase", memberDetails).isValid());
  }


  @Test
  public void parsesSimplePropertyCorrectly() throws Exception {
    assertTrue(new PartTree("findByText", memberDetails).isValid());
  }

  @Test
  public void parsesAndPropertiesCorrectly() throws Exception {
    assertTrue(new PartTree("findByTextAndNumber", memberDetails).isValid());
  }

  @Test
  public void parsesOrPropertiesCorrectly() throws Exception {
    assertTrue(new PartTree("findByTextOrNumber", memberDetails).isValid());
  }

  @Test
  public void parsesCombinedAndAndOrPropertiesCorrectly() throws Exception {
    assertTrue(new PartTree("findByTextAndEnumerOrNumber", memberDetails).isValid());

  }

  @Test
  public void parsesOrderBy() throws Exception {
    assertTrue(new PartTree("findByTextOrderByTextAsc", memberDetails).isValid());
    assertTrue(new PartTree("findByTextOrderByTextAscNumberDesc", memberDetails).isValid());
  }

  @Test
  public void detectsIgnoreAllCase() throws Exception {
    assertTrue(new PartTree("findByTextAllIgnoreCase", memberDetails).isValid());
    assertTrue(new PartTree("findByTextAllIgnoreCaseOrderByTextAsc", memberDetails).isValid());
  }

  @Test
  public void detectsSpecificIgnoreCase() throws Exception {
    assertTrue(new PartTree("findByTextIgnoreCaseAndNumber", memberDetails).isValid());
    assertTrue(new PartTree("findByTextIgnoringCaseAndNumber", memberDetails).isValid());
  }

  @Test
  public void parsesLessThanEqualCorrectly() {
    assertTrue(new PartTree("findByTextIsLessThanEqual", memberDetails).isValid());
    assertTrue(new PartTree("findByTextLessThanEqual", memberDetails).isValid());

  }

  @Test
  public void parsesGreaterThanEqualCorrectly() {
    assertTrue(new PartTree("findByTextIsGreaterThanEqual", memberDetails).isValid());
    assertTrue(new PartTree("findByTextGreaterThanEqual", memberDetails).isValid());
  }

  @Test
  public void parsesRegexKeywordCorrectly() {
    assertTrue(new PartTree("findByTextRegex", memberDetails).isValid());
    assertTrue(new PartTree("findByTextMatchesRegex", memberDetails).isValid());
    assertTrue(new PartTree("findByTextMatches", memberDetails).isValid());
  }

  @Test
  public void parsesStartingWithKeywordCorrectly() {
    assertTrue(new PartTree("findByTextStartsWith", memberDetails).isValid());
    assertTrue(new PartTree("findByTextStartingWith", memberDetails).isValid());
    assertTrue(new PartTree("findByTextIsStartingWith", memberDetails).isValid());
  }

  @Test
  public void parsesEndingWithKeywordCorrectly() {
    assertTrue(new PartTree("findByTextEndsWith", memberDetails).isValid());
    assertTrue(new PartTree("findByTextEndingWith", memberDetails).isValid());
    assertTrue(new PartTree("findByTextIsEndingWith", memberDetails).isValid());
  }

  @Test
  public void parsesContainingKeywordCorrectly() {
    assertTrue(new PartTree("findByTextIsContaining", memberDetails).isValid());
    assertTrue(new PartTree("findByTextContains", memberDetails).isValid());
    assertTrue(new PartTree("findByTextContaining", memberDetails).isValid());
  }

  @Test
  public void parsesAfterKeywordCorrectly() {
    assertTrue(new PartTree("findByDateAfter", memberDetails).isValid());
    assertTrue(new PartTree("findByDateIsAfter", memberDetails).isValid());
  }

  @Test
  public void parsesBeforeKeywordCorrectly() {
    assertTrue(new PartTree("findByDateIsBefore", memberDetails).isValid());
    assertTrue(new PartTree("findByDateBefore", memberDetails).isValid());
  }

  @Test
  public void parsesLikeKeywordCorrectly() {
    assertTrue(new PartTree("findByTextLike", memberDetails).isValid());
    assertTrue(new PartTree("findByTextIsLike", memberDetails).isValid());
  }

  @Test
  public void parsesNotLikeKeywordCorrectly() {
    assertTrue(new PartTree("findByTextNotLike", memberDetails).isValid());
    assertTrue(new PartTree("findByTextIsNotLike", memberDetails).isValid());
  }

  @Test
  public void parsesContainingCorrectly() {
    assertTrue(new PartTree("findByTextContainingOrTextContainingAllIgnoringCase", memberDetails)
        .isValid());
  }

  @Test
  public void detectPropertyWithOrKeywordPart() {
    assertTrue(PartTree.isValid("findByOrder"));
  }

  @Test
  public void detectPropertyWithAndKeywordPart() {
    assertTrue(PartTree.isValid("findByAnders"));
  }

  @Test
  public void detectPropertyPathWithOrKeywordPart() {
    assertTrue(PartTree.isValid("findByOrderId"));
  }

  @Test
  public void disablesFindFirstKImplicitIfNotPresent() {
    PartTree partTree = new PartTree("findByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == null);
  }

  @Test
  public void identifiesFindFirstImplicit() {
    PartTree partTree = new PartTree("findFirstByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == null);

    partTree = new PartTree("findTopByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == null);
  }

  @Test
  public void identifiesFindFirst1Explicit() {
    PartTree partTree = new PartTree("findFirst1ByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 1);

    partTree = new PartTree("findTop1ByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 1);
  }

  @Test
  public void identifiesFindFirstKExplicit() {
    PartTree partTree = new PartTree("findFirst10ByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 10);

    partTree = new PartTree("findTop10ByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 10);
  }

  @Test
  public void identifiesFindFirstKUsersExplicit() {
    PartTree partTree = new PartTree("findFirst10NumberByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 10);

    partTree = new PartTree("findTop10NumberByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 10);
  }

  @Test
  public void identifiesFindFirstKDistinctUsersExplicit() {
    PartTree partTree = new PartTree("findDistinctFirst10NumberByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 10);

    partTree = new PartTree("findDistinctTop10NumberByText", memberDetails);
    assertTrue(partTree.isValid() && partTree.getMaxResults() == 10);
  }

  @Test
  public void parsesIsNotContainingCorrectly() throws Exception {
    assertTrue(new PartTree("findByTextIsNotContaining", memberDetails).isValid());
    assertTrue(new PartTree("findByTextIsNotContaining", memberDetails).isValid());
    assertTrue(new PartTree("findByTextNotContaining", memberDetails).isValid());
  }

  @Test
  public void parsesInContainingCorrectly() {
    assertTrue(new PartTree("findByTextIn", memberDetails).isValid());
  }


  @Test
  public void detectsDistinctCorrectly() throws Exception {
    for (String prefix : PREFIXES) {
      PartTree partTree = new PartTree(prefix + "DistinctByText", memberDetails);
      assertTrue(partTree.isValid() && partTree.isDistinct());
      partTree = new PartTree(prefix + "DistinctTextByText", memberDetails);
      assertTrue(partTree.isValid() && partTree.isDistinct());
      partTree = new PartTree(prefix + "DistinctTop100TextByText", memberDetails);
      assertTrue(partTree.isValid() && partTree.isDistinct());
      partTree = new PartTree(prefix + "DistinctFirstByText", memberDetails);
      assertTrue(partTree.isValid() && partTree.isDistinct());
      partTree = new PartTree(prefix + "DistinctTopTextByText", memberDetails);
      assertTrue(partTree.isValid() && partTree.isDistinct());

    }
  }

  @Test
  public void optionsAfterQueryPrefix() throws Exception {
    for (String prefix : PREFIXES) {
      test(prefix, ArrayUtils.addAll(PROPERTIES, new String[] {"Distinct", "First", "Top", "By"}));
    }
  }


  @Test
  public void optionsAfterDistinct() throws Exception {
    for (String prefix : PREFIXES) {
      prefix += "Distinct";
      test(prefix, ArrayUtils.addAll(PROPERTIES, new String[] {"First", "Top", "By"}));
    }
  }

  @Test
  public void optionsAfterLimit() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : LIMIT) {
          String query = prefix + distinct + limit;
          test(query, ArrayUtils.addAll(PROPERTIES, new String[] {"[Number]", "By"}));
        }
      }
    }
  }

  @Test
  public void optionsAfterLimitNumber() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : LIMIT) {
          for (String number : NUMBERS) {
            String query = prefix + distinct + limit + number;

            if (number == "")
              test(query, ArrayUtils.addAll(PROPERTIES, new String[] {"By", "[Number]"}));
            else
              test(query, ArrayUtils.addAll(PROPERTIES, new String[] {"By"}));
          }
        }
      }
    }
  }


  @Test
  public void optionsAfterField() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : TEST_LIMIT) {
          for (String field : PROPERTIES) {
            test(prefix + distinct + limit + field, new String[] {"By"});
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterBy() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : TEST_LIMIT) {
          for (String field : ArrayUtils.addAll(PROPERTIES, "")) {
            test(prefix + distinct + limit + field + "By", PROPERTIES);
          }
        }
      }
    }
  }



  @Test
  public void optionsAfterPredicateStringField() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : TEST_LIMIT) {
          for (String field : ArrayUtils.addAll(PROPERTIES, "")) {
            test(prefix + distinct + limit + field + "ByText", ArrayUtils.addAll(
                ArrayUtils.addAll(STRING_OP, ArrayUtils.addAll(CONJUCTIONS, "")),
                ArrayUtils.addAll(ArrayUtils.addAll(IGNORE_CASE, ALL_IGNORE_CASE), "OrderBy")));
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterPredicateNumberField() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : TEST_LIMIT) {
          for (String field : ArrayUtils.addAll(PROPERTIES, "")) {

            test(
                prefix + distinct + limit + field + "ByNumber",
                (String[]) ArrayUtils.addAll(
                    ArrayUtils.addAll(NUMBER_OP, ArrayUtils.addAll(CONJUCTIONS, "")),
                    ArrayUtils.addAll(ArrayUtils.addAll(IGNORE_CASE, ALL_IGNORE_CASE), "OrderBy")));
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterPredicateDateField() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : TEST_LIMIT) {
          for (String field : ArrayUtils.addAll(PROPERTIES, "")) {

            test(prefix + distinct + limit + field + "ByDate", ArrayUtils.addAll(
                ArrayUtils.addAll(DATE_OP, ArrayUtils.addAll(CONJUCTIONS, "")),
                ArrayUtils.addAll(ArrayUtils.addAll(IGNORE_CASE, ALL_IGNORE_CASE), "OrderBy")));
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterPredicateprimitiveNumberField() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : TEST_LIMIT) {
          for (String field : ArrayUtils.addAll(PROPERTIES, "")) {

            test(prefix + distinct + limit + field + "ByPrimitiveInt", ArrayUtils.addAll(
                ArrayUtils.addAll(PRIMITE_NUMBER_OP, ArrayUtils.addAll(CONJUCTIONS, "")),
                ArrayUtils.addAll(ArrayUtils.addAll(IGNORE_CASE, ALL_IGNORE_CASE), "OrderBy")));
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterOperator() throws Exception {
    for (String prefix : PREFIXES) {
      for (String distinct : DISTINCT) {
        for (String limit : TEST_LIMIT) {
          for (String field : ArrayUtils.addAll(PROPERTIES, "")) {
            for (String fieldPred : PROPERTIES) {
              for (String operator : new String[] {"Null", "In", "IsIn"}) {

                test(prefix + distinct + limit + field + "By" + fieldPred + operator,
                    ArrayUtils.addAll(CONJUCTIONS, ArrayUtils.addAll(
                        ArrayUtils.addAll(IGNORE_CASE, ArrayUtils.addAll(ALL_IGNORE_CASE, "")),
                        "OrderBy")));
              }
            }
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterIs() throws Exception {

    test(
        "findByTextIs",
        ArrayUtils.addAll(
            this.STRING_IS_OP,
            ArrayUtils.addAll(ArrayUtils.addAll(CONJUCTIONS, ""),
                ArrayUtils.addAll(ArrayUtils.addAll(IGNORE_CASE, ALL_IGNORE_CASE), "OrderBy"))));

  }


  @Test
  public void optionsAfterOrConjuction() throws Exception {
    for (String fieldPred : PROPERTIES) {
      for (String operator : new String[] {"Null", "In", "IsIn", ""}) {
        test("findBy" + fieldPred + operator + "Or", ArrayUtils.addAll(PROPERTIES, "derBy"));

      }
    }
  }

  @Test
  public void optionsAfterAndConjuction() throws Exception {
    for (String fieldPred : PROPERTIES) {
      for (String operator : new String[] {"Null", "In", "IsIn", ""}) {
        test("findBy" + fieldPred + operator + "And", PROPERTIES);

      }
    }
  }

  @Test
  public void optionsAfterIgnore() throws Exception {
    for (String ignore : IGNORE_CASE) {
      test(
          "findByText" + ignore,
          ArrayUtils.addAll(
              STRING_OP,
              ArrayUtils.addAll(ArrayUtils.addAll(CONJUCTIONS, ""),
                  ArrayUtils.addAll(ALL_IGNORE_CASE, "OrderBy"))));
    }
  }

  @Test
  public void optionsAfterAllIgnore() throws Exception {
    for (String fieldPred : PROPERTIES) {
      for (String ignore : ArrayUtils.addAll(IGNORE_CASE, "")) {
        for (String allIgnore : ALL_IGNORE_CASE) {
          test("findBy" + fieldPred + ignore + allIgnore, new String[] {"", "OrderBy"});
        }
      }
    }
  }

  @Test
  public void optionsAfterOrderBy() throws Exception {
    for (String fieldPred : PROPERTIES) {
      for (String operator : new String[] {"Null", "In", ""}) {
        for (String ignore : ArrayUtils.addAll(IGNORE_CASE, "")) {
          for (String allIgnore : ALL_IGNORE_CASE) {
            test("findBy" + fieldPred + operator + ignore + allIgnore + "OrderBy", PROPERTIES);
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterOrderField() throws Exception {
    for (String fieldPred : PROPERTIES) {
      for (String operator : new String[] {"Null", "In", "IsIn", ""}) {
        for (String ignore : ArrayUtils.addAll(IGNORE_CASE, "")) {
          for (String allIgnore : ALL_IGNORE_CASE) {
            test("findBy" + fieldPred + operator + ignore + allIgnore + "OrderBy" + fieldPred,
                new String[] {"Asc", "Desc"});
          }
        }
      }
    }
  }

  @Test
  public void optionsAfterOrderFieldDirection() throws Exception {
    for (String fieldPred : PROPERTIES) {
      for (String operator : new String[] {"Null", "In", "IsIn", ""}) {
        for (String ignore : ArrayUtils.addAll(IGNORE_CASE, "")) {
          for (String allIgnore : ALL_IGNORE_CASE) {
            for (String direction : new String[] {"Asc", "Desc"}) {

              test("findBy" + fieldPred + operator + ignore + allIgnore + "OrderBy" + fieldPred
                  + direction, ArrayUtils.addAll(PROPERTIES, ""));
            }
          }
        }
      }
    }
  }



  private List<String> generateOptions(String prefix, String[] suffixes) {
    List<String> options = new ArrayList<String>();
    for (String suffix : suffixes) {
      options.add(prefix + suffix);
    }
    return options;
  }

  private List<String> generateOptions(String prefix, List<String> suffixes) {
    List<String> options = new ArrayList<String>();
    for (String suffix : suffixes) {
      options.add(prefix + suffix);
    }
    return options;
  }

  private void assertEqualsList(List<String> list1, List<String> list2) {
    Collections.sort(list1);
    Collections.sort(list2);
    assertEquals(list1, list2);

  }


}
