export interface FPGSUser 
{
    json(): unknown;
    ok: any;
    id: number;
    role: string;
    username: string;
    firstName: string;
    lastName: string;
    active: boolean | null;
    created: string | Date | number | null;
    //svi posle ovoga mogu biti null ili da ne postoje
    modified?: string | Date | number | null;
    dob?: string | Date | number | null;
    password?: string | null;
    email?: string | null;
    phone?: string | null;
    organization?: string | null;
    comment?: string | null;
}