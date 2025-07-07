export interface User 
{
    id: number;
    role: string;
    username: string;
    firstName: string;
    lastName: string;
    active: boolean | null;
    created: string | Date | number | null;
    //svi posle ovoga mogu biti null ili da ne postoje
    modified?: string | Date | number | null;
    password?: string | null;
    email?: string | null;
    phone?: string | null;
}