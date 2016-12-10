package com.runsascoded.deps

import org.hammerlab.test.Suite
import org.hammerlab.test.files.TmpFiles
import org.hammerlab.test.resources.File
import org.hammerlab.test.matchers.files.FileMatcher.fileMatch

class InterPackageDepsToDotTest extends Suite with TmpFiles {
  test("guac") {
    val testDir = File("guac-readsets").path
    val outFile = tmpFile()

    InterPackageDepsToDot.main(Array(testDir, outFile))

    outFile should fileMatch("guac-readsets/golden.dot")
  }
}
