package org.springframework.roo.addon.finder.addon.parser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.model.JavaType;

/**
 * Represents a single search expression (which are joined using And/Or operators).
 * This expression needs a property to define the condition. 
 * Optionally, an operator can be set after the property to perform an operation over it. 
 * Furthermore, {@literal IgnoreCase} option is available to be added to any property.
 * 
 * @author Paula Navarro
 * @since 2.0
 */
public class Part {

  private static final Pattern IGNORE_CASE = Pattern.compile("Ignor(ing|e)Case");

  // Contains property metadata and name
  private final Pair<FieldMetadata, String> property;

  // Operator type
  private final Type type;
  private String operatorGroup = "";
  private String operator = null;

  private IgnoreCaseType ignoreCase = IgnoreCaseType.NEVER;


  // Stores which ignore case option (IgnoreCase or IgnoringCase) has been used
  private String ignoreCaseString = "";



  /**
   * Creates a new {@link Part} from a condition stored into source .
   * 
   * @param source the search criteria
   * @param fields entity properties
   */
  public Part(String source, List<FieldMetadata> fields) {

    // Extract and remove IgnoreCase option from source
    String partToUse = detectAndSetIgnoreCase(source);

    // Extract property
    this.property = PartTree.extractValidProperty(partToUse, fields);

    // Remove property from source to process the operator
    if (property != null) {
      partToUse = partToUse.substring(property.getRight().length());
    }

    // Extract operator information
    Pair<Type, String> type = Type.extractOperator(partToUse);
    this.type = type.getLeft();
    this.operator = type.getRight();
    this.operatorGroup = Type.extractOperatorGroup(operator);
  }



  /**
   * Detects if expression contains IgnoreCase option and removes it.
   * 
   * @param expression
   * @return expression without IgnoreCase option.
   */
  private String detectAndSetIgnoreCase(String expression) {

    Matcher matcher = IGNORE_CASE.matcher(expression);
    String result = expression;

    if (matcher.find()) {
      ignoreCase = IgnoreCaseType.ALWAYS;
      ignoreCaseString = matcher.group(0);
      result =
          expression.substring(0, matcher.start())
              + expression.substring(matcher.end(), expression.length());
    }

    return result;
  }


  /**
   * Returns how many method parameters are bound by this part.
   * 
   * @return
   */
  public int getNumberOfArguments() {
    return type.getNumberOfArguments();
  }

  /**
   * Returns the property metadata and name of this expression. 
   * If any property is not defined, returns {@literal null}.
   * 
   * @return Pair of property metadata and property name
   */
  public Pair<FieldMetadata, String> getProperty() {
    return property;
  }

  /**
   * @return the operator {@link Type}
   */
  public Type getType() {
    return type;
  }

  /**
   * Returns whether the search criteria referenced should be matched
   * ignoring case.
   * 
   * @return
   */
  public IgnoreCaseType shouldIgnoreCase() {
    return ignoreCase;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return (property != null ? property.getRight() : "").concat(operator != null ? operator : "")
        .concat(ignoreCaseString);
  }

  /**
   * Returns operators supported by the search expression property
   * @return
   */
  public List<String> getSupportedOperators() {
    if (property == null) {
      return null;
    }

    List<String> typeKeywords = new ArrayList<String>();
    List<Type> types = Type.getOperators(property.getLeft().getFieldType());

    // Check if operator group is an operator
    boolean removePrefix = Type.ALL_KEYWORDS.contains(operatorGroup);

    // Get operators
    for (Type type : types) {
      for (String keyword : type.getKeywords()) {

        // Add operator if it does not belong to any group and operator group is not defined
        if (StringUtils.isBlank(operatorGroup)
            && !StringUtils.startsWithAny(keyword, Type.PREFIX_GROUP)) {
          typeKeywords.add(keyword);
        }

        // Add operator if it belongs to the operator group specified
        if (StringUtils.isNotBlank(operatorGroup) && keyword.startsWith(operatorGroup)) {

          //If operator group is an operator as well, we need to remove the operator group prefix from operators (to avoid it appears two times )
          if (removePrefix) {
            typeKeywords.add(StringUtils.substringAfter(keyword, operatorGroup));
          } else {
            typeKeywords.add(keyword);
          }
        }
      }
    }

    // If there is not an operator group, all operator groups are available
    if (StringUtils.isBlank(operatorGroup)) {
      typeKeywords.addAll(Arrays.asList(Type.PREFIX_GROUP));
    }

    return typeKeywords;

  }


  /**
   * Returns true if the Part or search criteria has a property defined
   * @return
   */
  public boolean hasProperty() {
    return property != null;
  }

  /**
   * Returns true if the Part or search criteria has an operator
   * @return
   */
  public boolean hasOperator() {
    return type != null && StringUtils.isNotEmpty(operator);
  }


  /**
   * Returns the operator group. If it does not have a group returns an empty string.
   * @return
   */
  public String getOperatorGroup() {
    return operatorGroup;
  }


  /**
   * Returns operator keyword. If operator is not defined, returns an empty string.
   * @return
   */
  public String getOperator() {
    return operator;
  }

}
