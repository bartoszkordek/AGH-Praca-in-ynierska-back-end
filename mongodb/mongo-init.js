db.createUser(
    {
        user: "adminPracaInz",
        pwd: "thisP@sswordNeed2BeChange",
        roles: [
            {role: "readWrite", db: "databasePracaInz"}
        ]
    }
);