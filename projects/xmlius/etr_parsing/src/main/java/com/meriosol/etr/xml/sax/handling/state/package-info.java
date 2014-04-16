/**
 * This package contains Hierarchy of states for ETR events XML tree.<br>
 *     NOTE: Though it was conceived initially as State GoF pattern impl, it turns out better to view it
 *     as simplified "Chain of Responsibility" pattern. Likely basic stack would be simpler solution
 *     (event processing of XML tree of elements naturally mirrors stack management).
 */
package com.meriosol.etr.xml.sax.handling.state;

