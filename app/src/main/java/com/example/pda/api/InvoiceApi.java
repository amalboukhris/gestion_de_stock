package com.example.pda.api;


import com.example.pda.Models.CreateInvoiceRequest;
import com.example.pda.Models.CreateInvoiceResponse;
import com.example.pda.Models.InvoiceLineAndTagsDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface InvoiceApi {

    @GET("invoice/user/{userId}")
    Call<List<CreateInvoiceRequest>> getInvoicesByUserId(@Path("userId") int userId);

    @GET("/invoices/{invoiceId}/details")
    Call<List<InvoiceLineAndTagsDto>> getInvoiceDetails(@Path("invoiceId") int invoiceId);
    @POST("/invoices")
    Call<CreateInvoiceResponse> createInvoice(@Body CreateInvoiceRequest request);
}
