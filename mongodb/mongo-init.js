db.createUser(
    {
        user: "adminPracaInz",
        pwd: "thisPAsswordNeed2BeChange",
        roles: [
            {role: "readWrite", db: "databasePracaInz"}
        ]
    }
);