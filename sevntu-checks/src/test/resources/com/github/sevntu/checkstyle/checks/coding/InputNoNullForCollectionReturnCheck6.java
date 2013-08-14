public class InputNoNullForCollectionReturnCheck6
{
    class DecoratableResourceMapping extends DecoratableResource {

        public DecoratableResourceMapping(ResourceMapping mapping) {
            if(projects == null || projects.length == 0)
                return;
        }
    }
}
