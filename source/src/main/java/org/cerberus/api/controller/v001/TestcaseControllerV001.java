/**
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.api.controller.v001;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.api.dto.v001.TestcaseDTOV001;
import org.cerberus.api.mapper.v001.TestcaseMapperV001;
import org.cerberus.api.service.PublicApiAuthenticationService;
import org.cerberus.crud.service.ITestCaseService;
import org.cerberus.exception.CerberusException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MorganLmd
 */
@AllArgsConstructor
@RestController
@Api(tags = "Testcase")
@RequestMapping(path = "/public/testcases")
public class TestcaseControllerV001 {

    private static final String API_VERSION_1 = "X-API-VERSION=1";
    private static final String API_KEY = "X-API-KEY";
    private final ITestCaseService testCaseService;
    private final TestcaseMapperV001 testcaseMapper;
    private final PublicApiAuthenticationService apiAuthenticationService;
    private static final Logger LOG = LogManager.getLogger(TestcaseControllerV001.class);

    @ApiOperation("Get all testcases filtered by test")
    @ApiResponse(code = 200, message = "ok", response = TestcaseDTOV001.class, responseContainer = "List")
    @GetMapping(path = "/{testFolderId}", headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TestcaseDTOV001> findTestcasesByTest(
            @PathVariable("testFolderId") String testFolderId,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) {
        this.apiAuthenticationService.authenticate(principal, apiKey);
        return this.testCaseService.findTestCaseByTest(testFolderId)
                .stream()
                .map(this.testcaseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @ApiOperation("Get a testcase filtered by testFolderId and testCaseFolderId")
    @ApiResponse(code = 200, message = "ok", response = TestcaseDTOV001.class)
    @GetMapping(path = "/{testFolderId}/{testcaseId}", headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TestcaseDTOV001 findTestcaseByTestAndTestcase(
            @PathVariable("testFolderId") String testFolderId,
            @PathVariable("testcaseId") String testcaseId,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) throws CerberusException {
        this.apiAuthenticationService.authenticate(principal, apiKey);
        return this.testcaseMapper.toDTO(this.testCaseService.findTestCaseByKeyWithDependencies(testFolderId, testcaseId, true).getItem());
    }

    @ApiOperation("Create a new Testcase")
    @ApiResponse(code = 200, message = "ok")
    @PostMapping(headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TestcaseDTOV001 createTestcase(
            @RequestBody TestcaseDTOV001 newTestcase,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) throws CerberusException {
        this.apiAuthenticationService.authenticate(principal, apiKey);

        return this.testcaseMapper.toDTO(
                this.testCaseService.createTestcaseWithDependenciesAPI(
                        this.testcaseMapper.toEntity(newTestcase)
                )
        );
    }

    @ApiOperation("Update a Testcase")
    @ApiResponse(code = 200, message = "ok")
    @PutMapping(path = "/{testFolderId}/{testcaseId}", headers = {API_VERSION_1}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TestcaseDTOV001 update(
            @PathVariable("testcaseId") String testcaseId,
            @PathVariable("testFolderId") String testFolderId,
            @RequestBody TestcaseDTOV001 testcaseToUpdate,
            @RequestHeader(name = API_KEY, required = false) String apiKey,
            Principal principal) throws CerberusException {

        this.apiAuthenticationService.authenticate(principal, apiKey);

        return this.testcaseMapper.toDTO(
                this.testCaseService.updateTestcaseAPI(
                        testFolderId,
                        testcaseId,
                        this.testcaseMapper.toEntity(testcaseToUpdate)
                ));
    }
}
