package deptools.plugin.scala.parser.filter

import deptools.plugin.scala.parser.Dependency
import org.junit.{Assert, Test}

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 24.mar.2010
 * Time: 00:28:03
 * To change this template use File | Settings | File Templates.
 */

class DependencyMatcherTest{

  @Test
  def testDependencyMatcher{

    var dm = new DependencyMatcher("ab.")
    var depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( false, dm.matches(depPath))

    dm = new DependencyMatcher("g1.*")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))

    dm = new DependencyMatcher("g2.+")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))

    dm = new DependencyMatcher(".*a11.*")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))

    dm = new DependencyMatcher(".*a1xx1.*")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( false, dm.matches(depPath))
  }
}


class DependencyFilterIncludeExcludeImplTest{

  @Test
  def test{
    var dm = new DependencyFilterIncludeExcludeImpl("ab.", "xx")
    var depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( false, dm.matches(depPath))

    dm = new DependencyFilterIncludeExcludeImpl(null, null)
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))

    dm = new DependencyFilterIncludeExcludeImpl("xx", null)
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( false, dm.matches(depPath))

    dm = new DependencyFilterIncludeExcludeImpl("g2.*", null)
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))

    dm = new DependencyFilterIncludeExcludeImpl("g1:a2", null)
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))

    dm = new DependencyFilterIncludeExcludeImpl(null, "g2.*")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( false, dm.matches(depPath))

    dm = new DependencyFilterIncludeExcludeImpl(null, "g1:a2")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( false, dm.matches(depPath))

    //this should go through since g1:a1 is the first dependency
    dm = new DependencyFilterIncludeExcludeImpl(null, "g1:a1")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))

    //include match and exclude does not match -> ok
    dm = new DependencyFilterIncludeExcludeImpl("g.*", "xx")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( true, dm.matches(depPath))


    //include matches but exclude excludes it -> no match
    dm = new DependencyFilterIncludeExcludeImpl("g1:.*", "g1:a2")
    depPath = List(
      new Dependency("g1", "a1", "jar", "v", "compile"),
      new Dependency("g1", "a2", "jar", "v", "compile"),
      new Dependency("g2", "a11", "jar", "v", "compile")).toArray
    Assert.assertEquals( false, dm.matches(depPath))
  }
}