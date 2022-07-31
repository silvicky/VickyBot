package Picture;

public class FakeMsg {
    public String text;
    public FakeUser user;
    public FakeMsg(FakeUser user)
    {
        this.user=user;
    }
    public void setText(String text){this.text=text;}
}
