export interface PGSArtikal {
  id?: number;
  name: string;
  barcode?: string;

  opis?: string;
  napomena?: string;
  proizvodjac?: string;

  stok?: number;
  odjel?: number;

  porez?: number;
  poreznaGrupa?: number;

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
}