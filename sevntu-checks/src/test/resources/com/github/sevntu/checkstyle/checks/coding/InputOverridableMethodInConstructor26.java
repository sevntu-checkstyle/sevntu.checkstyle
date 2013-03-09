package com.puppycrawl.tools.checkstyle.coding;

public class InputOverridableMethodInConstructor26
{

    class ULCWorkbenchPartSite
    {

        public ULCWorkbenchPartSite()
        {
            init();
            initPart(part, input);
        }

        @SuppressWarnings("unchecked")
        private void initPart(IWorkbenchPart<?> part, Object input)
        {
            try {
                ((IWorkbenchPart<Object>) part).init(this, input);
            }
            catch (InvalidInputException e) {
                // TODO  any special handling
                throw new RuntimeException(e);
            }
        }

        @Override
        public IWorkbenchPart<?> getComponent()
        {
            return (IWorkbenchPart<?>) super.getComponent();
        }

        public IWorkbenchPart<?> getPart()
        {
            return getComponent();
        }

        private void init()
        {

            getPart();
        }
    }
}
