export interface PGSArtikal {
  comment: string | null | undefined;
  id?: number;
  name: string;
  barcode?: string;
  
  // ID kategorije za lak rad sa formama i selektima
  kategorijaId?: number;
  
  // Kompletan objekat kategorije (ako se radi fetch sa join-om)
  kategorija?: PGSArtikalKategorija;

  created?: Date | string;
  updated?: Date | string;

  price1?: number;
  price2?: number;
  price3?: number;
  price4?: number;
  price5?: number;
  price6?: number;
  
  // Dodajemo i active polje jer smo ga pomenuli zbog soft-delete-a
  active?: boolean;
}

export interface PGSArtikalKategorija {
  id: number;
  name: string;
  created?: Date | string;
  updated?: Date | string;
  // artikli?: PGSArtikal[]; // Opciono, zavisi da li ih šalješ sa backenda
}