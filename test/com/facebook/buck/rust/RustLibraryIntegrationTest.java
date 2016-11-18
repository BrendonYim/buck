/*
 * Copyright 2016-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.rust;

import static org.junit.Assert.assertThat;

import com.facebook.buck.testutil.integration.ProjectWorkspace;
import com.facebook.buck.testutil.integration.TemporaryPaths;
import com.facebook.buck.testutil.integration.TestDataHelper;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;


public class RustLibraryIntegrationTest {
  @Rule
  public TemporaryPaths tmp = new TemporaryPaths();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void ensureRustIsAvailable() throws IOException, InterruptedException {
    RustAssumptions.assumeRustCompilerAvailable();
  }

  @Test
  public void rustLibraryCompilerArgs() throws IOException, InterruptedException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "binary_with_library", tmp);
    workspace.setUp();

    assertThat(
        workspace
            .runBuckCommand(
                "run",
                "--config",
                "rust.rustc_flags=--this-is-a-bad-option",
                "//messenger:messenger")
            .getStderr(),
        Matchers.containsString("Unrecognized option: 'this-is-a-bad-option'."));
  }

  @Test
  public void rustLibraryCompilerArgs2() throws IOException, InterruptedException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "binary_with_library", tmp);
    workspace.setUp();

    assertThat(
        workspace
            .runBuckCommand(
                "run",
                "--config",
                "rust.rustc_flags=--verbose --this-is-a-bad-option",
                "//messenger:messenger")
            .getStderr(),
        Matchers.containsString("Unrecognized option: 'this-is-a-bad-option'."));
  }

  @Test
  public void rustLibraryRuleCompilerArgs() throws IOException, InterruptedException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "binary_with_library", tmp);
    workspace.setUp();

    assertThat(
        workspace
            .runBuckCommand("run", "//messenger:messenger_flags")
            .getStderr(),
        Matchers.containsString("Unrecognized option: 'this-is-a-bad-option'."));
  }


  @Test
  public void binaryWithLibrary() throws IOException, InterruptedException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "binary_with_library", tmp);
    workspace.setUp();

    assertThat(
        workspace.runBuckCommand("run", "//:hello").assertSuccess().getStdout(),
        Matchers.allOf(
            Matchers.containsString("Hello, world!"),
            Matchers.containsString("I have a message to deliver to you")));
  }

  @Test
  public void binaryWithAliasedLibrary() throws IOException, InterruptedException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "binary_with_library", tmp);
    workspace.setUp();

    assertThat(
        workspace.runBuckCommand("run", "//:hello_alias").assertSuccess().getStdout(),
        Matchers.allOf(
            Matchers.containsString("Hello, world!"),
            Matchers.containsString("I have a message to deliver to you")));
  }
}