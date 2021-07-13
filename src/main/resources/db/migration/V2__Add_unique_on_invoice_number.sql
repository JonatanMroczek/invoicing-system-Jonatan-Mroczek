ALTER TABLE public.invoices
    ADD CONSTRAINT unique_invoice_number UNIQUE ("number");