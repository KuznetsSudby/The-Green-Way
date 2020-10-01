package kusu.thegreenway.checks

import com.android.SdkConstants
import com.android.SdkConstants.VALUE_MATCH_CONSTRAINT
import com.android.SdkConstants.VALUE_MATCH_PARENT
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import org.w3c.dom.Attr


val ISSUE_CONSTRAINT_LAYOUT_MATCH_PARENT_ATTRIBUTE_DETECTOR = Issue.create(
    "ConstraintLayoutMatchParentAttribute",
    "Always use 0dp (\"match_constraint\") instead of \"match_parent\"",
    "Using \"match_parent\" in child nodes of ConstraintLayout is not recommended. It can cause unexpected behaviour.",
    CORRECTNESS,
    6,
    Severity.WARNING,
    Implementation(
        ConstraintLayoutMatchParentAttributeDetector::class.java,
        Scope.RESOURCE_FILE_SCOPE
    )
)

class ConstraintLayoutMatchParentAttributeDetector : LayoutDetector() {
    override fun getApplicableAttributes() =
        listOf(SdkConstants.ATTR_LAYOUT_WIDTH, SdkConstants.ATTR_LAYOUT_HEIGHT)

    override fun visitAttribute(context: XmlContext, attribute: Attr) {

        if (attribute.ownerElement.parentNode.nodeName != SdkConstants.CLASS_CONSTRAINT_LAYOUT.defaultName()) {
            return
        }

        if (attribute.value != VALUE_MATCH_PARENT) {
            return
        }

        context.report(
            ISSUE_CONSTRAINT_LAYOUT_MATCH_PARENT_ATTRIBUTE_DETECTOR,
            attribute,
            context.getValueLocation(attribute),
            "Please don't use ${attribute.value} in ConstraintLayout}",
            if (attribute.localName == SdkConstants.ATTR_LAYOUT_WIDTH) {
                createFix(
                    attribute,
                    SdkConstants.ATTR_LAYOUT_START_TO_START_OF,
                    SdkConstants.ATTR_LAYOUT_END_TO_END_OF
                )
            } else {
                createFix(
                    attribute,
                    SdkConstants.ATTR_LAYOUT_TOP_TO_TOP_OF,
                    SdkConstants.ATTR_LAYOUT_BOTTOM_TO_BOTTOM_OF
                )
            }
        )
    }

    private fun createFix(attribute: Attr, constraintOne: String, constraintTwo: String): LintFix? {
        val build = fix()
            .alternatives()
            .add(createSimpleFix(attribute))
            .add(createCompositeFixWithConstraints(constraintOne, constraintTwo, attribute))
        return build.build()
    }

    private fun createSimpleFix(attribute: Attr) =
        fix()
            .set(attribute.namespaceURI, attribute.localName, VALUE_MATCH_CONSTRAINT)
            .name("Replace \"match_parent\" with \"0dp\"")
            .autoFix()
            .build()

    private fun createCompositeFixWithConstraints(
        constraintOne: String,
        constraintTwo: String,
        attribute: Attr
    ): LintFix? {
        return fix()
            .composite()
            .name("Replace \"match_parent\" with \"0dp\" and parent constraints")
            .type(LintFix.GroupType.COMPOSITE)
            .add(
                fix()
                    .set(SdkConstants.AUTO_URI, constraintOne, SdkConstants.ATTR_PARENT)
                    .build()
            )
            .add(
                fix()
                    .set(SdkConstants.AUTO_URI, constraintTwo, SdkConstants.ATTR_PARENT)
                    .build()
            )
            .add(
                fix()
                    .set(
                        attribute.namespaceURI,
                        attribute.localName,
                        VALUE_MATCH_CONSTRAINT
                    )
                    .build()
            )
            .build()
    }
}