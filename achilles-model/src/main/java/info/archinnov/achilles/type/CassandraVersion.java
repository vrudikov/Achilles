/*
 * Copyright (C) 2012-2016 DuyHai DOAN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.archinnov.achilles.type;

/**
 * Define which version of Cassandra you want Achilles to generate source code against
 * <br/>
 * <br/>
 * Below are the list of supported features for each Cassandra version:
 * <br/>
 * <br/>
 * <table border="1">
 *     <thead>
 *         <tr>
 *             <th>Cassandra version</th>
 *             <th>Supported features</th>
 *             <th>Related JIRA(s)</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>2.1.X</td>
 *             <td>All existing features</td>
 *             <td>N/A</td>
 *         </tr>
 *         <tr>
 *             <td>2.2.X</td>
 *             <td>
 *                 <ul>
 *                     <li>JSON Syntax</li>
 *                     <li>User Defined Functions</li>
 *                     <li>User Defined Aggregates</li>
 *                 </ul>
 *             </td>
 *             <td>
 *                 <ul>
 *                     <li>CASSANDRA-7970</li>
 *                     <li>CASSANDRA-7395, CASSANDRA-7526, CASSANDRA-7562, CASSANDRA-7740, CASSANDRA-7781, CASSANDRA-7929,
 *                     CASSANDRA-7924, CASSANDRA-7812, CASSANDRA-8063, CASSANDRA-7813, CASSANDRA-7708</li>
 *                     <li>CASSANDRA-8053</li>
 *                 </ul>
 *             </td>
 *             </tr>
 *         <tr>
 *             <td>3.0.X</td>
 *             <td>
 *                 <ul>
 *                     <li>Materialized Views</li>
 *                     <li>Support for IN restrictions on any partition key component or clustering key
 *                     as well as support for EQ and IN multicolumn restrictions has been added to
 *                     UPDATE and DELETE statement</li>
 *                     <li>Support for single-column and multi-column slice restrictions (>, >=, <= and <)
 *                     has been added to DELETE statements</li>
 *                 </ul>
 *             </td>
 *             <td>
 *                 <ul>
 *                     <li>CASSANDRA-6477</li>
 *                 </ul>
 *             </td>
 *             </tr>
 *         <tr>
 *             <td>3.1</td>
 *             <td>Nothing</td>
 *             <td>N/A</td>
 *         </tr>
 *         <tr>
 *             <td>3.2</td>
 *             <td>Add support for type casting in selection clause</td>
 *             <td>CASSANDRA-10310</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * And below is the impact of each new feature on **Achilles**-generated code:
 *
 * <table border="1">
 *     <thead>
 *         <tr>
 *             <td>Cassandra Feature</td>
 *             <td>Achilles generated code</td>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>JSON Syntax</td>
 *             <td>
 *                 <ul>
 *                     <li>manager.crud().insertJson(String json)</li>
 *                     <li>manager.dsl().....where().xxx().Eq_FromJSON()</li>
 *                     <li>manager.dsl().select().allColumnsAsJSON_FromBaseTable().....where()......getJSON()</li>
 *                     <li>manager.dsl().update().....xxx().Set_FromJSON()</li>
 *                     <li>manager.dsl().update().....if_xxx().Eq_FromJSON()</li>
 *                     <li>manager.dsl().delete().....if_xxx().Eq_FromJSON()</li>
 *                 </ul>
 *             </td>
 *         </tr>
 *         <tr>
 *             <td>User Defined Function/User Defined Aggregates</td>
 *             <td>
 *                 <ul>
 *                     <li>@FunctionRegistry is allowed</li>
 *                     <li><em>info.archinnov.achilles.generated.function.FunctionsRegistry</em> generated class</li>
 *                 </ul>
 *             </td>
 *         </tr>
 *         <tr>
 *             <td>Materialized Views</td>
 *             <td>
 *                 <ul>
 *                     <li>@MaterializedView is allowed</li>
 *                 </ul>
 *             </td>
 *         </tr>
 *         <tr>
 *             <td>Support for IN restrictions on clustering columns for UPDATE/DELETE</td>
 *             <td>
 *                 <ul>
 *                     <li>manager.dsl().update()...where()....xxx().IN(...)</li>
 *                     <li>manager.dsl().delete()...where()....xxx().IN(...)</li>
 *                 </ul>
 *             </td>
 *         </tr>
 *         <tr>
 *             <td>Support for multi-column slice restrictions (>, >=, <= and <) for DELETE</td>
 *             <td>
 *                 <ul>
 *                     <li>manager.dsl().delete()...where()....xxx().Gt(...)</li>
 *                     <li>manager.dsl().delete()...where()....xxx().Gt_And_Lt(...)</li>
 *                 </ul>
 *             </td>
 *         </tr>
 *         <tr>
 *             <td>Support for type casting in selection clause</td>
 *             <td>
 *                 <ul>
 *                     <li>manager.dsl().select().function(SystemFunctions.castAsxxx()...)</li>
 *                 </ul>
 *             </td>
 *         </tr>
 *     </tbody>
 * </table>
 */

public enum CassandraVersion {


    CASSANDRA_2_1_X,
    /**
     * New features:
     * <br/>
     * <ul>
     *     <li>User Defined Function, User Defined Aggregate</li>
     *     <li>JSON Syntax</li>
     * </ul>
     */
    CASSANDRA_2_2_X,
    /**
     * New features:
     * <br/>
     * <ul>
     *     <li>Materialized Views</li>
     *     <li>Support for IN restrictions on any partition key component or clustering key
     *          as well as support for EQ and IN multicolumn restrictions has been added to
     *          UPDATE and DELETE statement</li>
     *     <li>Support for single-column and multi-column slice restrictions (>, >=, <= and <)
     *          has been added to DELETE statements</li>
     * </ul>
     */
    CASSANDRA_3_0_X,
    CASSANDRA_3_1,
    /**
     * New features:
     * <br/>
     * <ul>
     *     <li>Add support for type casting in selection clause (CASSANDRA-10310)</li>
     * </ul>
     */
    CASSANDRA_3_2,
    CASSANDRA_3_3,
    CASSANDRA_3_4,
    CASSANDRA_3_5,
    /**
     * New features:
     * <br/>
     * <ul>
     *     <li>Add static column support to SASI index (CASSANDRA-11183)</li>
     *     <li>Allow per-partition LIMIT clause in CQL (CASSANDRA-7017)</li>
     *     <li>Support for non-frozen user-defined types, updating individual fields of user-defined types (CASSANDRA-7423)</li>
     * </ul>
     *
     */
    CASSANDRA_3_6,
    /**
     * New features:
     * <br/>
     * <ul>
     *     <li><strong>Stable</strong> SASI index with Support LIKE operator in prepared statements (CASSANDRA-11456)</li>
     * </ul>
     *
     */
    CASSANDRA_3_7,
    /**
     * New features:
     * <br/>
     * <ul>
     *     <li>Allow literal values in UDF/UDA parameters (CASSANDRA-10783)</li>
     * </ul>
     *
     */
    CASSANDRA_3_9
}