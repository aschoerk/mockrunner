package de.mockrunner.example;

public class MySQLAuthenticationStrategy implements AuthenticationStrategy
{
    private boolean isUserKnown;
    private boolean isPasswordOk;
    
    public boolean authenticate(String username, String password)
    {
        //do database stuff here
        isUserKnown = false;
        isPasswordOk = false;
        return false;
    }

    public boolean wasLastUserKnown()
    {
        return isUserKnown;
    }

    public boolean wasLastPasswordOk()
    {
        return isPasswordOk;
    }

}
