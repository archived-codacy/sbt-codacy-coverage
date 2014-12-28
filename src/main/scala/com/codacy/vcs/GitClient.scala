package com.codacy.vcs

import java.io.File

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder

class GitClient(cwd: File) {

  import scala.collection.JavaConversions._

  val repository = new RepositoryBuilder().findGitDir(cwd).build()

  def latestCommitUuid(): String = {
    val git = new Git(repository)
    val headRev = git.log().setMaxCount(1).call().head
    headRev.getName
  }

}
