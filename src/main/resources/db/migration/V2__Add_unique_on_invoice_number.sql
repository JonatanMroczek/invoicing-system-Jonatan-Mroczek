ALTER TABLE public.invoice
    ADD CONSTRAINT unique_invoice_number UNIQUE (number);