package com.github.sevntu.checkstyle.checks.coding;

public class InputSingleBreakOrContinueCheck
{

    public void forLoopMultipleContinue()
    {
        for (int i = 1; i <= 10; i++) // violation - 2 continue
        {
            if (i % 2 == 0)
            {
                continue;
            }

            if (i % 3 == 0)
            {
                continue;
            }

            System.out.println("i = " + i);
        }
    }

    public void forLoopMultipleBreak()
    {
        for (int i = 1; i <= 10; i++) // violation - 2 break
        {
            if (i % 2 == 0)
            {
                break;
            }

            if (i % 3 == 0)
            {
                break;
            }

            System.out.println("i = " + i);
        }
    }

    public void forLoopMultipleBreakAndContinue()
    {
        for (int i = 1; i <= 10; i++) // violation - 1 break and 1 continue
        {
            if (i % 2 == 0)
            {
                break;
            }

            if (i % 3 == 0)
            {
                continue;
            }

            System.out.println("i = " + i);
        }
    }

    public void forLoopSingleContinue()
    {
        for (int i = 1; i <= 10; i++) // OK - 1 continue
        {
            if (i % 2 == 0)
            {
                continue;
            }

            System.out.println("i = " + i);
        }
    }

    public void forLoopSingleBreak()
    {
        for (int i = 1; i <= 10; i++) // OK - 1 break
        {
            if (i % 2 == 0)
            {
                break;
            }

            System.out.println("i = " + i);
        }
    }

    public void whileLoopMultipleContinue()
    {
        while (true) // violation - 2 continue
        {
            if (true)
            {
                continue;
            }

            if (true)
            {
                continue;
            }

            System.out.println("violation - 2 continue");
        }
    }

    public void whileLoopMultipleBreak()
    {
        while (true) // violation - 2 break
        {
            if (true)
            {
                break;
            }

            if (true)
            {
                break;
            }

            System.out.println("violation - 2 break");
        }
    }

    public void whileLoopMultipleBreakAndContinue()
    {
        while (true) // violation - 1 continue and 1 break
        {
            if (true)
            {
                continue;
            }

            if (true)
            {
                break;
            }

            System.out.println("violation - 1 continue and 1 break");
        }
    }

    public void whileLoopSingleContinue()
    {
        while (true) // OK - 1 continue
        {
            if (true)
            {
                continue;
            }

            System.out.println("OK - 1 continue");
        }
    }

    public void whileLoopSingleBreak()
    {
        while (true) // OK - 1 break
        {
            if (true)
            {
                break;
            }

            System.out.println("OK - 1 break");
        }
    }

    public void doWhileLoopMultipleContinue()
    {
        do// violation - 2 continue
        {
            if (true)
            {
                continue;
            }

            if (true)
            {
                continue;
            }

            System.out.println("violation - 2 continue");
        }
        while (true);
    }

    public void doWhileLoopMultipleBreak()
    {
        do // violation - 2 break
        {
            if (true)
            {
                break;
            }

            if (true)
            {
                break;
            }

            System.out.println("violation - 2 break");
        }
        while (true);
    }

    public void doWhileLoopMultipleBreakAndContinue()
    {
        do // violation - 1 continue and 1 break
        {
            if (true)
            {
                continue;
            }

            if (true)
            {
                break;
            }

            System.out.println("violation - 1 continue and 1 break");
        }
        while (true);
    }

    public void doWhileLoopSingleContinue()
    {
        do // OK - 1 continue
        {
            if (true)
            {
                continue;
            }

            System.out.println("OK - 1 continue");
        }
        while (true);
    }

    public void doWhileLoopSingleBreak()
    {
        do // OK - 1 break
        {
            if (true)
            {
                break;
            }

            System.out.println("OK - 1 break");
        }
        while (true);
    }

    public String switchInsideFor(String value)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) // OK - Switch block
        {
            final char chr = value.charAt(i);
            switch (chr) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            case '\"':
                sb.append("&quot;");
                break;
            case '&':
                sb.append(chr);
                break;
            default:
                sb.append(chr);
                break;
            }
        }
        return sb.toString();
    }

    public void switchInsideWhile(String value)
    {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) // OK - Switch block
        {
            final char chr = value.charAt(i);
            switch (chr) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            case '\"':
                sb.append("&quot;");
                break;
            case '&':
                sb.append(chr);
                break;
            default:
                sb.append(chr);
                break;
            }
        }
    }

    public void switchInsideDoWhile(String value)
    {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        do // OK - Switch block
        {
            final char chr = value.charAt(i);
            switch (chr) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            case '\"':
                sb.append("&quot;");
                break;
            case '&':
                sb.append(chr);
                break;
            default:
                sb.append(chr);
                break;
            }
        }
        while (true);
    }

    public void forNestedLoop()
    {
        for (int i = 1; i <= 10; i++)// OK - Outer loop
        {
            while (true) // violation - Inner loop: 1 continue and 1 break
            {
                if (true)
                {
                    continue;
                }

                if (true)
                {
                    break;
                }

                System.out.println("violation - 1 continue and 1 break");
            }
        }
    }

    public void whileNestedLoop()
    {
        while (true)// OK - Outer loop
        {
            do // violation - Inner loop: 2 break
            {
                if (true)
                {
                    break;
                }

                if (true)
                {
                    break;
                }

                System.out.println("violation - 2 break");
            }
            while (true);
        }
    }

    public void doWhileNestedLoop()
    {
        do // OK - Outer loop
        {
            for (int i = 1; i <= 10; i++) // violation - Inner loop 2 break
            {
                if (i % 2 == 0)
                {
                    break;
                }

                if (i % 3 == 0)
                {
                    break;
                }

                System.out.println("i = " + i);
            }
        }
        while (true);
    }
}
