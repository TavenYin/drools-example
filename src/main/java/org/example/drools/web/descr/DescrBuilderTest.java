package org.example.drools.web.descr;

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.mvel.DrlDumper;
import org.junit.Test;

/**
 * 更多用法参考：
 * <a href="https://github.com/kiegroup/drools/blob/main/drools-test-coverage/test-compiler-integration/src/test/java/org/drools/mvel/compiler/lang/api/DescrBuilderTest.java">DescrBuilderTest.java</a>
 *
 * @author tianwen.yin
 */
public class DescrBuilderTest {

    @Test
    public void buildDrl() {
        PackageDescr pkg = DescrFactory.newPackage().name( "org.test" )
                .newRule().name( "org.test" )
                .lhs().and()
                .or()
                .pattern().id( "$x", false ).type( "Integer" ).constraint( "this > 10" ).end()
                .pattern().id( "$x", false ).type( "Integer" ).constraint( "this < 20" ).end()
                .end()
                .pattern().type( "Integer" ).constraint( "this == $x" ).constraint( "this == 42" ).end()
                .end().end()
                .rhs( "" )
                .end()
                .end().getDescr();

        String drl = new DrlDumper().dump( pkg );
        System.out.println( drl );
    }

}
