package com.SovietHouseholdAppliances.EventManager.model;

import retrofit2.Call;
import retrofit2.http.*;

public interface EventManagerApi {
    @GET("token")
    Call<String> getToken(@Header("Authorization") String authorization);
    @GET("user")
    Call<User[]> getAllUsers(@Header("Authorization") String authorization);
    @POST("user")
    Call<String> createUser(@Header("Authorization") String authorization, @Body User user);
    @GET("user/id/{id}")
    Call<User> getUserById(@Header("Authorization") String authorization, @Path("id") int id);
    @DELETE("user/id/{id}")
    Call<String> deleteUser(@Header("Authorization") String authorization, @Path("id") int id);
    @PUT("user/id/{id}")
    Call<String> updateUser(@Header("Authorization") String authorization, @Path("id") int id, @Body User user);
    @GET("user/name/{user}")
    Call<User> getUserByName(@Header("Authorization") String authorization, @Path("user") String username);
    @GET("user/me")
    Call<User> getAuthenticatedUser(@Header("Authorization") String authorization);
    @GET("event")
    Call<Event[]> getAllEvents(@Header("Authorization") String authorization, @Query("equipmentId") Integer equipmentId, @Query("from") Long from, @Query("until") Long until);
    @POST("event")
    Call<String> createEvent(@Header("Authorization") String authorization, @Body Event event);
    @GET("event/{id}")
    Call<Event> getEvent(@Header("Authorization") String authorization, @Path("id") int id);
    @DELETE("event/{id}")
    Call<String> deleteEvent(@Header("Authorization") String authorization, @Path("id") int id);
    @PUT("event/{id}")
    Call<String> updateEvent(@Header("Authorization") String authorization, @Path("id") int id, @Body Event event);
    @GET("equipment")
    Call<Equipment[]> getAllEquipments(@Header("Authorization") String authorization, @Query("eventId") Integer eventId);
    @POST("equipment")
    Call<String> createEquipment(@Header("Authorization") String authorization, @Body Equipment equipment);
    @GET("equipment/{id}")
    Call<Equipment> getEquipment(@Header("Authorization") String authorization, @Path("id") int id);
    @DELETE("equipment/{id}")
    Call<String> deleteEquipment(@Header("Authorization") String authorization, @Path("id") int id);
    @PUT("equipment/{id}")
    Call<String> updateEquipment(@Header("Authorization") String authorization, @Path("id") int id, @Body Equipment equipment);
}