/*
 * Copyright 2014 the original author or authors.
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
package net.kuujo.copycat;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

import net.kuujo.copycat.log.Log;
import net.kuujo.copycat.state.StateType;

/**
 * The replica is the primary interface for creating fault-tolerant services
 * with CopyCat. CopyCat replicas represent a single node which can be a
 * member of a cluster of any size. Underlying the replica is a state machine
 * that is implemented in the form of a command registry. Commands registered
 * to the replica's state machine can be called remotely. Commands that modify
 * state will be internally logged and replicated to other members of the
 * cluster.<p>
 * 
 * Cluster configurations are also subject to change. The {@link ClusterConfig}
 * class is an <code>Observable</code> class, so replicas will monitor the
 * cluster configuration for changes. When a member is added to or removed from
 * the cluster configuration, if the current replica is the cluster leader then
 * it will safely log and replicate the configuration change to the rest of the
 * cluster. This process is done in a manner that ensures safety in the consensus
 * algorithm during cluster membership changes.
 *
 * @author Jordan Halterman
 */
public interface Replica {

  /**
   * Returns the replica address.
   * 
   * @return The replica address.
   */
  String address();

  /**
   * Returns the cluster configuration.
   * 
   * @return The replica's cluster configuration.
   */
  ClusterConfig config();

  /**
   * Returns the replicated log.
   * 
   * @return The replica's log.
   */
  Log log();

  /**
   * Sets the maximum memory usage of the state machine.
   *
   * @param max The maximum allowed memory usage of the state machine.
   * @return The replica instance.
   */
  Replica setMaxMemoryUsage(long max);

  /**
   * Returns the maximum allowed memory usage.
   *
   * @return The maximum allowed memory usage for the state machine.
   */
  long getMaxMemoryUsage();

  /**
   * Sets the replica election timeout.
   * 
   * @param timeout The election timeout.
   * @return The replica instance.
   */
  Replica setElectionTimeout(long timeout);

  /**
   * Returns the replica election timeout.
   * 
   * @return The election timeout.
   */
  long getElectionTimeout();

  /**
   * Sets the replica heartbeat interval.
   * 
   * @param interval The interval at which the node should send heartbeat messages.
   * @return The replica instance.
   */
  Replica setHeartbeatInterval(long interval);

  /**
   * Returns the replica heartbeat interval.
   * 
   * @return The replica heartbeat interval.
   */
  long getHeartbeatInterval();

  /**
   * Returns a boolean indicating whether adaptive timeouts are enabled.
   * 
   * @return Indicates whether adaptive timeouts are enabled.
   */
  public boolean isUseAdaptiveTimeouts();

  /**
   * Indicates whether the replica should use adaptive timeouts.
   * 
   * @param useAdaptive Indicates whether to use adaptive timeouts.
   * @return The replica.
   */
  public Replica useAdaptiveTimeouts(boolean useAdaptive);

  /**
   * Returns the adaptive timeout threshold.
   * 
   * @return The adaptive timeout threshold.
   */
  public double getAdaptiveTimeoutThreshold();

  /**
   * Sets the adaptive timeout threshold.
   * 
   * @param threshold The adaptive timeout threshold.
   * @return The replica.
   */
  public Replica setAdaptiveTimeoutThreshold(double threshold);

  /**
   * Returns a boolean indicating whether majority replication is required for write
   * operations.
   * 
   * @return Indicates whether majority replication is required for write operations.
   */
  public boolean isRequireWriteMajority();

  /**
   * Sets whether majority replication is required for write operations.
   * 
   * @param require Indicates whether majority replication should be required for writes.
   * @return The replica.
   */
  public Replica setRequireWriteMajority(boolean require);

  /**
   * Returns a boolean indicating whether majority synchronization is required for read
   * operations.
   * 
   * @return Indicates whether majority synchronization is required for read operations.
   */
  public boolean isRequireReadMajority();

  /**
   * Sets whether majority synchronization is required for read operations.
   * 
   * @param require Indicates whether majority synchronization should be required for read
   *          operations.
   * @return The replica.
   */
  public Replica setRequireReadMajority(boolean require);

  /**
   * Registers a handler to be called when the replica transitions to a new state.
   *
   * @param handler A handler to be called when the replica transitions to a new state.
   * @return The replica instance.
   */
  Replica transitionHandler(Handler<StateType> handler);

  /**
   * Returns a boolean indicating whether the replica is a follower.
   *
   * @return Indicates whether the replica is a follower.
   */
  boolean isFollower();

  /**
   * Returns a boolean indicating whether the replica is a candidate.
   *
   * @return Indicates whether the replica is a candidate.
   */
  boolean isCandidate();

  /**
   * Returns a boolean indicating whether the replica is a leader.
   *
   * @return Indicates whether the replica is a leader.
   */
  boolean isLeader();

  /**
   * Returns the current leader address.
   *
   * @return The current leader's address/
   */
  String getCurrentLeader();

  /**
   * Submits a command to the replication service.
   * 
   * @param command The name of the command to submit.
   * @param args An object of arguments required by the command implementation.
   * @param resultHandler An asynchronous handler to be called with the command result.
   * @return The replica.
   */
  <R> Replica submitCommand(String command, JsonObject args, Handler<AsyncResult<R>> resultHandler);

  /**
   * Starts the node.
   * 
   * @return The replica instance.
   */
  Replica start();

  /**
   * Starts the node.
   * 
   * @param doneHandler An asynchronous handler to be called once the replica has been
   *          started.
   * @return The replica instance.
   */
  Replica start(Handler<AsyncResult<Void>> doneHandler);

  /**
   * Stops the replica.
   */
  void stop();

  /**
   * Stops the replica.
   * 
   * @param doneHandler An asynchronous handler to be called once the replica has been
   *          stopped.
   */
  void stop(Handler<AsyncResult<Void>> doneHandler);

}
